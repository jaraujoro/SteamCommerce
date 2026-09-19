package com.SteamCommerce.item.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.item.dto.ItemResponseDto;
import com.SteamCommerce.item.dto.ResultadoSync;
import com.SteamCommerce.item.entity.ItemEntity;
import com.SteamCommerce.item.mapper.ItemMapper;
import com.SteamCommerce.item.repository.ItemRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> listarItems() {
        List<ItemEntity> items = itemRepository.findAllWithRelations();
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
            return ResultadoSync.vacio();
        }

        Map<String, ItemEntity> existentes = buscarExistentes(items);

        List<ItemEntity> aGuardar = items.stream().map(dto -> resolverEntidad(dto, existentes)).toList();

        itemRepository.saveAll(aGuardar);

        int actualizados = (int) aGuardar.stream().filter(e -> existentes.containsKey(e.getAssetId())).count();

        int nuevos = aGuardar.size() - actualizados;

        return new ResultadoSync(nuevos, actualizados, items.size());
    }

    private Map<String, ItemEntity> buscarExistentes(List<ItemRequestDto> items) {
        Set<String> assetIds = items.stream().map(ItemRequestDto::getAssetId).filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());

        if (assetIds.isEmpty()) {
            return Map.of();
        }

        return itemRepository.findAllByAssetIdIn(assetIds).stream()
                .collect(Collectors.toMap(ItemEntity::getAssetId, e -> e));
    }

    private ItemEntity resolverEntidad(ItemRequestDto dto, Map<String, ItemEntity> existentes) {
        ItemEntity existente = existentes.get(dto.getAssetId());

        if (existente != null) {
            itemMapper.updateEntity(existente, dto);
            return existente;
        }

        return itemMapper.toEntity(dto);
    }

}