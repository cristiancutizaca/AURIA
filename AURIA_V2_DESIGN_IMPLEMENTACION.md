# AURIA v2: Flow + Nebula + Pulse + Studio (Halo)

Estado: **código en rama de rediseño, pendiente compilación Android/Windows y pruebas en dispositivo**.

Esta entrega es una implementación real de la primera migración visual, no una maqueta.
Se reutiliza el sistema de reproducción, biblioteca, ajustes y navegación existente.

## Cuatro estilos en Ajustes → Temas

| Estilo | Experiencia |
|---|---|
| Flow | Hero horizontal inmersivo, portada protagonista, controles flotantes, paleta índigo. |
| Nebula | Hero espacial con aureolas y composición vertical, violeta/lavanda. |
| Pulse | Hero compacto, biblioteca en filas y chrome reducido para explorar rápido. |
| Studio | Presentación editorial tipo consola con ondas estáticas y chrome compacto. |

Los estilos se guardan como los identificadores `auria_flow`, `auria_nebula`, `auria_pulse`, `auria_studio` usando **la preferencia de temas existente**, sin migración de base de datos.
Los usuarios que conservan `auria_halo` reciben el layout Flow; sus colores guardados se mantienen.
Los temas heredados y los estilos manuales de reproductor o navegación permanecen disponibles.

## Cambios principales

- Nuevas paletas AURIA en el Theme Studio y selector visual dedicado de cuatro tarjetas.
- Inicio: reemplazo del hero fotográfico heredado por cuatro composiciones realmente distintas.
- Biblioteca: grilla de tarjetas para Flow/Nebula y filas compactas para Pulse/Studio.
- Reproductor: usa diseños Flow/Halo/Pulse/Folio según el estilo cuando el modo previo era Halo; permite mantener otros modos explícitos.
- Minirreproductor + dock: cuando estaba seleccionado el antiguo estilo conectado, se reinterpreta como Floating + FloatingDock o UltraCompact + Minimal. Respeta preferencias explícitas diferentes.
- Onboarding: tema oscuro inicial Flow e isotipo de AURIA con círculo orbital.
- Encabezado de colecciones: «AURIA · Orbits» para evitar continuidad visual literal con Levyra.
- Tests de identidad y selección de modos; tests de preferencias antiguas.

## Fuera de alcance y riesgos

No se ha reescrito todo el onboarding, Descubrir, las carátulas ni el AURIA DJ avanzado. Tampoco se ha realizado una migración completa de Windows. No es una APK.

`git diff --check` fue satisfactorio; el quality gate se detuvo en validaciones F-Droid del repositorio base y el wrapper de Gradle no pudo descargar su distribución por falta de red. Ninguna prueba de compilación Android/Windows, UI o dispositivo puede considerarse aprobada.

## Aplicación al repositorio actual

**Recomendado**: crear rama, ejecutar comprobaciones y luego integrar a `main`.

```bash
git checkout -b feature/auria-v2-design
git apply --check AURIA_V2_Flow_Nebula_Pulse_Studio.patch
git apply AURIA_V2_Flow_Nebula_Pulse_Studio.patch
python3 scripts/ai_quality_gate.py --profile fast
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

**Importante:** el archivo `.patch` se genera sobre `AURIA-main-0.1.2-modificado.zip` proporcionado por el usuario. Antes de aplicarlo a otra rama, comparar esa base con el `main` de GitHub, para no sobrescribir trabajo posterior.
