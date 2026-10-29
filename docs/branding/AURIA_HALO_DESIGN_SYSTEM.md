# AURIA Halo: sistema visual V1

**Concepto:** el halo es la identidad de AURIA; se reserva para marca y acciones relevantes, evitando saturar la pantalla.

| Rol | Color |
|---|---|
| Fondo | `#0A0F26` |
| Superficie | `#171D38` |
| Elevación | `#252A49` |
| Violeta | `#8B5CF6` |
| Lavanda | `#C8B4FF` |
| Acento accesible | `#B09AFF` |
| Coral | `#FF9E8A` |
| Texto | `#F1F5FF` |
| Secundario | `#ADB7CD` |

## Recursos
- `auria_icon.svg`: diseño vectorial.
- `auria_wordmark.svg`: logotipo horizontal.
- `auria_icon_preview.png`: PNG compacto de referencia.
- `ic_auria_halo_foreground.xml`: icono adaptativo Android.
- `ic_auria_halo_monochrome.xml`: icono monocromo Android 13+.
- `mipmap-*`: PNG de compatibilidad, compactos para el repositorio.

## Compatibilidad
Se conservan Media3, Room, las colas, las descargas, los favoritos y preferencias.
La nueva paleta Halo se usa por defecto en instalaciones sin tema guardado.
El dock por defecto es ConnectedDock y el mini reproductor Connected, ambos a 60 dp.
Los usuarios con ajustes visuales persistidos mantienen sus preferencias. Pueden elegir manualmente Halo, ConnectedDock y Connected.

El acceso a Buscar se conserva durante estas fases. No se alteran los contratos del reproductor, la sesión de medios ni el orden de la cola.

## Próximas fases
Fase 3: Inicio/Biblioteca/Reproductor. Fase 4: favoritos físicos con copia/movimiento. Fase 6: DJ avanzado. Fase 7: Windows. Este commit no incluye esas funciones.

## Validación pendiente
Compilación Android, tests completos y prueba en dispositivo. Las comprobaciones del instalador, XML y PNG se realizaron sobre el paquete local, no sobre una compilación de `main`.
