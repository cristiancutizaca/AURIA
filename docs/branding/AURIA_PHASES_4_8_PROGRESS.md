# AURIA Halo · avance en fases 4 a 8 (29/10/2026)

## Fase 4 · Gestión física de Me gusta
**Implementado parcialmente (Android):** desde el detalle de favoritos, copiar archivos locales de la lista completa o de una selección múltiple a una carpeta elegida mediante el selector seguro del sistema. El trabajo se ejecuta en IO, evita sobrescribir nombres existentes y limpia el archivo destino si falla a mitad. El original nunca se elimina. Al finalizar se muestra resumen de resultados y se solicita escaneo rápido de biblioteca si hubo copias.

**Pendiente para completar:** mover el original con confirmación y permisos de MediaStore, detectar duplicados por contenido (actualmente nombre), autoguardado opcional, una carpeta recordada, tareas persistentes/reanudables si se cierra la app, pruebas reales con proveedores SAF.

## Fase 5 · Descubrir
**Implementado parcialmente:** la tarjeta editorial usa tonos violetas/lavanda de AURIA y menor inclinación de las carátulas. Se conservan accesos, contenido real, carga, estado offline y acciones.

**Pendiente:** reorganizar completamente Descubrir, búsqueda y filtros a partir de nuevas maquetas y pruebas de navegación.

## Fase 6 · AURIA DJ
**Implementado parcialmente:** transición gráfica con paleta AURIA y anillo orbital. Se conserva el mezclador actual: no se añadieron BPM, beat matching, key matching, análisis de cues o stem separation.

**Pendiente:** interfaz profesional, ajustes y pruebas de DJ automático, ritmo, tonalidad y transición.

## Fase 7 · Windows
**Implementado parcialmente:** tema oscuro de Desktop alineado con AURIA Halo; sin afectar el motor libvlc ni actualizaciones o configuraciones guardadas. Tema claro y rediseño completo de pantallas quedan pendientes.

## Fase 8 · Validación
**Añadido:** test unitario de identificación de canciones locales para exportación de favoritos.
**Bloqueado en esta sesión:** compilación Android y Desktop, tests de integración, revisiones visuales, pruebas en dispositivo. No confundir cambios subidos a GitHub con APK comprobada.

## Invariantes
- Sin borrado ni movimiento del archivo original.
- Sin datos nuevos en Room ni permisos globales de almacenamiento.
- Sin cambiar Media3/libvlc, cola o datos favoritos.
- Solo pistas con fuente Offline + URI content:// se copian.
- La carpeta de destino la decide el usuario cada vez.
- Preferencias visuales previas se preservan; nuevo color Desktop aplica cuando se usa el tema oscuro.
