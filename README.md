# 🌾 Harvest Indicator — Fabric Mod for Minecraft 1.21.x

Muestra un ícono flotante **"!"** sobre los cultivos que están listos para cosechar.

## Cultivos soportados

| Cultivo | Maduro cuando... |
|---|---|
| Trigo | age == 7 |
| Papa | age == 7 |
| Zanahoria | age == 7 |
| Remolacha | age == 3 |
| Nether Wart | age == 3 |
| Cocoa Beans | age == 2 |
| Melón | bloque de melón presente |
| Calabaza | bloque de calabaza presente |
| Sweet Berry Bush | age >= 2 |

## Requisitos

- Java 21+
- Fabric Loader >= 0.16.5
- Fabric API >= 0.102.0

## Compilar

```bash
./gradlew build
```

El `.jar` se genera en `build/libs/harvest-indicator-1.0.0.jar`

## Instalar

1. Instala [Fabric Loader](https://fabricmc.net/use/) para Minecraft 1.21.1
2. Descarga [Fabric API](https://modrinth.com/mod/fabric-api)
3. Copia `harvest-indicator-1.0.0.jar` y `fabric-api.jar` en tu carpeta `.minecraft/mods/`
4. ¡Listo!

## Estructura del proyecto

```
src/
├── main/java/com/harvestindicator/
│   ├── HarvestIndicatorMod.java       ← Entry point común
│   └── CropReadinessChecker.java      ← Lógica de detección de madurez
├── client/java/com/harvestindicator/
│   ├── HarvestIndicatorClient.java    ← Entry point cliente
│   └── HarvestIconRenderer.java       ← Renderiza el ícono flotante
└── main/resources/
    ├── fabric.mod.json
    └── assets/harvestindicator/
        ├── textures/icons/ready.png   ← Ícono "!" amarillo 16x16
        └── lang/es_es.json
```
