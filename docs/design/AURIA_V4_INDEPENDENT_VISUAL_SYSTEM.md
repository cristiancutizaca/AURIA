# AURIA V4 · Sistema visual independiente

Estado: código integrado en `main`, pendiente de prueba manual en Android y Windows.
No se afirma que APK, EXE o MSI estén compilados y validados.

## Problema identificado

Comparativa de capturas reales del 1 de noviembre de 2026:
- AURIA y Levyra compartían disposición de Inicio (cabecera, radio, Tu órbita, tarjetas), Buscar, Explorar, minirreproductor y navegación.
- Cambiar azules por morados y añadir un halo no era diferenciación suficiente.
- El minirreproductor y la navegación tapaban parte del contenido en varios recorridos.

## Principios de la nueva dirección

1. **Escucha primero:** foco protagonista en música actual y acciones relevantes, no clon de la página inicial heredada.
2. **Jerarquía de señales:** sistema AURIA con estaciones, numeración, radar, consola y archivo personal.
3. **Composición propia:** cambiar distribución, agrupación y affordances, no solo paletas y redondeos.
4. **Un motor, varias experiencias:** Flow, Nebula, Pulse y Studio cambian geometría sin duplicar bases de datos ni reproductores.
5. **Privacidad y preservación:** no migrar ni perder música local, favoritos, playlists, colas ni descargas por un cambio visual.
6. **Interacción real:** cualquier tarjeta o control visible debe responder; evitar falsos botones decorativos.

## Android: componentes activos

| Superficie | Archivo/entrada | Objetivo |
| --- | --- | --- |
| Fondo visual | `ui/AuriaIndependentBackdrop.kt` | Geometría independiente por modo |
| Inicio | `ui/AuriaIndependentHome.kt` | Escucha, radar y archivo |
| Navegación | `ui/AuriaIndependentShell.kt` (`AuriaCommandDock`) | Destino prioritario y comandos |
| Minirreproductor | `ui/AuriaIndependentShell.kt` (`AuriaTransportStrip`) | Transporte de dos niveles |
| Reproductor expandido | `ui/player/AuriaIndependentPlayerDeck.kt` | Estructura AURIA dedicada con cuatro geometrías y slots del reproductor existente, preservando audio, gestos y paneles |
| Buscar | `ui/AuriaIndependentSearch.kt` | Consola de búsqueda |
| Explorar | `ui/AuriaIndependentExplore.kt` | Radar y catálogo musical |
| Biblioteca | `ui/library/AuriaLibraryAtlas.kt` | Bóveda personal organizada |
| Recognize/Identify | `ui/recognition/AuriaRecognitionHalo.kt` | Identificación bajo demanda |
| Enrutamiento | `ui/LevyraApp.kt` | Selección de superficie por modo AURIA |

Nota: los nombres internos Kotlin heredados de Levyra permanecen donde hacen falta para compatibilidad.
La presencia de esos identificadores internos no implica que el diseño visual deba seguir pareciéndose.

## Windows: componentes implementados en esta iteración

- `screens/AuriaDesktopListeningStage.kt`: portada editorial original, órbitas y acciones rápidas.
- `screens/AuriaDesktopHome.kt`: Inicio de escritorio independiente con escenario de escucha, comandos de destinos, señales, ranking personal e historial, conectado a las acciones existentes. Se activa desde `LevyraRoot.kt`.
- `screens/HomeScreen.kt`: se conserva como referencia de compatibilidad, no como Inicio principal.
- `screens/SearchScreen.kt`: cabecera de terminal musical e input asimétrico.
- `screens/DiscoverScreen.kt`: encabezado radar para rankings con anillos orbitales.
- `screens/LibraryScreen.kt`: bóveda original con indicador de archivo y categorías en métricas.
- `components/navigation/AuriaDesktopCommandRail.kt`: navegación por índice propia; activada desde `LevyraRoot.kt`.

Las operaciones se conectan al modelo y a los callbacks existentes. El motor de audio no se altera.

## Personalidades visuales

- **Flow**: márgenes amplios, eje asimétrico, piezas editoriales y ritmo visual.
- **Nebula**: órbitas de distinta escala y superficies envolventes.
- **Pulse**: altura contenida, señales densas y menor tiempo de navegación.
- **Studio**: superficies geométricas y lenguaje de instrumentación musical.

## Revisiones de aceptación en dispositivo a cargo del usuario

1. Abrir Inicio, Buscar, Explorar y Biblioteca en los cuatro modos.
2. Comparar cada pantalla con las capturas de Levyra, especialmente composición, no solo color.
3. Cambiar de canción y probar el minirreproductor: tocar portada, pausa, siguiente, gesto para abrir, y vuelta.
4. Revisar que ninguna lista quede oculta detrás del minirreproductor y navegación inferior.
5. Probar listas vacías, sin Internet, música local, biblioteca de más de 1.600 canciones y cambios de cuenta.
6. Probar rotación, pantallas pequeñas, escala de texto grande, tema claro/oscuro y accesibilidad.
7. Verificar que Identificar requiere permiso, devuelve resultado/no coincidencia y permite abrir la canción.
8. En Windows: cambiar tamaños de ventana, reproducir, usar búsqueda, abrir rankings y biblioteca.
9. Confirmar que favoritos, colas, descargas y playlists siguen intactos al actualizar.

## Alcance y trabajo todavía no cerrado

- UI Android integrada en `main`: Inicio, Buscar, Explorar, Biblioteca, dock, transporte y un deck expandido propio en modo retrato. En orientación horizontal, vídeo, radio, modales y secundarios permanecen rutas de compatibilidad que requieren auditoría visual.
- Windows sigue compartiendo algunos widgets históricos que requieren una auditoría visual adicional.
- Build y tests automatizados siguen sin validación concluyente en esta entrega; verificar antes de publicar.
- Nunca presentar una integración de reconocimiento musical como compatible con cualquier aplicación: Android puede bloquear captura interna por políticas de cada app.

Esta hoja sirve para impedir volver al enfoque antiguo de «mismo diseño con otros colores».
