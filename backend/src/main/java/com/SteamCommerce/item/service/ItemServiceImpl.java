package com.SteamCommerce.item.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.item.dto.ItemResponseDto;
import com.SteamCommerce.item.dto.ResultadoSync;
import com.SteamCommerce.item.entity.ItemEntity;
import com.SteamCommerce.item.mapper.ItemMapper;
import com.SteamCommerce.item.repository.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 100;

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> listarItems() {
        List<ItemEntity> items = itemRepository.findAll();
        return items.stream().map(itemMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void crearItem(ItemRequestDto dto) {

        if (dto == null) {
            throw new BadRequestException("El valor no puede ser nulo");
        }

        if (dto.getAssetId() == null || dto.getAssetId().trim().isEmpty()) {
            throw new BadRequestException("El identificador es obligatorio");
        }

        var existingItem = itemRepository.findByAssetId(dto.getAssetId());

        if (existingItem.isPresent()) {
            ItemEntity entity = existingItem.get();
            entity.setTradable(dto.getTradable());
            entity.setMarketable(dto.getMarketable());
            entity.setTradeCooldownUntil(dto.getTradeCooldownUntil());
            entity.setMarketTradableRestriction(dto.getMarketTradableRestriction());
            entity.setAmount(dto.getAmount());
            entity.setColor(dto.getColor());
            itemRepository.save(entity);
            return;
        }

        ItemEntity item = itemMapper.toEntity(dto);
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public ResultadoSync crearItemsEnBatch(List<ItemRequestDto> items) {

        if (items == null || items.isEmpty()) {
            return new ResultadoSync(0, 0, 0);
        }

        // 1. Traer TODOS los existentes con 1 sola query
        List<String> assetIds = items.stream()
                .map(ItemRequestDto::getAssetId)
                .filter(id -> id != null && !id.isBlank())
                .toList();

        Map<String, Long> existentesPorAssetId = new HashMap<>();
        if (!assetIds.isEmpty()) {
            itemRepository.findAllByAssetIdIn(assetIds)
                    .forEach(e -> existentesPorAssetId.put(e.getAssetId(), e.getIdItem()));
        }

        // 2. Separar en nuevos y existentes
        List<ItemRequestDto> nuevos = items.stream()
                .filter(i -> !existentesPorAssetId.containsKey(i.getAssetId()))
                .toList();

        List<ItemRequestDto> existentes = items.stream()
                .filter(i -> existentesPorAssetId.containsKey(i.getAssetId()))
                .toList();

        // 3. Batch UPDATE de existentes (mismos campos que tu crearItem actual)
        if (!existentes.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    """
                            UPDATE item
                            SET tradable = ?, marketable = ?, trade_cooldown_until = ?,
                                market_tradable_restriction = ?, amount = ?, color = ?
                            WHERE asset_id = ?
                            """,
                    existentes,
                    BATCH_SIZE,
                    (ps, dto) -> {
                        ps.setObject(1, dto.getTradable());
                        ps.setObject(2, dto.getMarketable());
                        ps.setTimestamp(3, dto.getTradeCooldownUntil() != null
                                ? Timestamp.valueOf(dto.getTradeCooldownUntil())
                                : null);
                        ps.setObject(4, dto.getMarketTradableRestriction());
                        ps.setObject(5, dto.getAmount());
                        ps.setString(6, dto.getColor());
                        ps.setString(7, dto.getAssetId());
                    });
        }

        // 4. Batch INSERT de nuevos
        if (!nuevos.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    """
                            INSERT INTO item (
                                public_id, asset_id, app_id, context_id, class_id, instance_id,
                                amount, name, market_name, market_hash_name, icon_url, color,
                                tradable, marketable, commodity, market_tradable_restriction,
                                trade_cooldown_until, id_tipo_item, id_rareza, id_heroe, creado_en
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """,
                    nuevos,
                    BATCH_SIZE,
                    (ps, dto) -> {
                        int i = 1;
                        ps.setString(i++, UUID.randomUUID().toString());
                        ps.setString(i++, dto.getAssetId());
                        ps.setString(i++, dto.getAppId());
                        ps.setString(i++, dto.getContextId());
                        ps.setString(i++, dto.getClassId());
                        ps.setString(i++, dto.getInstanceId());
                        ps.setObject(i++, dto.getAmount());
                        ps.setString(i++, dto.getName());
                        ps.setString(i++, dto.getMarketName());
                        ps.setString(i++, dto.getMarketHashName());
                        ps.setString(i++, dto.getIconUrl());
                        ps.setString(i++, dto.getColor());
                        ps.setObject(i++, dto.getTradable());
                        ps.setObject(i++, dto.getMarketable());
                        ps.setObject(i++, dto.getCommodity());
                        ps.setObject(i++, dto.getMarketTradableRestriction());
                        ps.setTimestamp(i++,
                                dto.getTradeCooldownUntil() != null ? Timestamp.valueOf(dto.getTradeCooldownUntil())
                                        : null);
                        ps.setObject(i++, dto.getIdTipoItem());
                        ps.setObject(i++, dto.getIdRareza());
                        ps.setObject(i++, dto.getIdHeroe());
                        ps.setTimestamp(i, Timestamp.valueOf(java.time.LocalDateTime.now()));
                    });
        }
        return new ResultadoSync(nuevos.size(), existentes.size(), items.size());
    }
}