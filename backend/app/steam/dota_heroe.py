import re

class SteamDotaHero:
    
    def extraer_heroe_de_item(self, desc):
        # Opción A: por tags
        for tag in desc.get('tags', []):
            if tag.get('category', '').lower() == 'hero':
                return tag.get('localized_tag_name')

        # Opción B: por texto "Used By: X" dentro de descriptions
        for d in desc.get('descriptions', []):
            match = re.match(r'Used By:\s*(.+)', d.get('value', ''))
            if match:
                return match.group(1).strip()

        return None