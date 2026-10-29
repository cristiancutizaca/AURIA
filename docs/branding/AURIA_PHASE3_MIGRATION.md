# AURIA Halo · Fase 3: inicio, biblioteca y reproductor

## Alcance
Rediseño visual incremental en Android, respetando el motor de audio actual. Se mantiene la navegación y todas las colecciones del usuario.

### Inicio
- Atmósfera nocturna con aureola estática detrás del contenido, sin animación infinita ni procesamiento adicional de imágenes.
- Nuevos acentos violeta, lavanda y coral.
- Se conservan secciones, búsqueda, selección de géneros y recomendaciones.

### Biblioteca
- Cabecera con isotipo decorativo orbital.
- Acceso a música local más compacto con los mismos controles y lógica de permisos.
- Favoritos destacados con gradiente tenue, borde y menor altura.

### Reproductor
- Modo Halo como predeterminado en nuevos perfiles; los ajustes existentes se respetan.
- Aureola cromática alrededor de la portada, tipografía menos pesada y barra inferior refinada.
- Media3, controles, gestos, cola, sesión, reproducción en segundo plano y modo vídeo se conservan.

## Seguridad y pruebas
No hay migraciones de Room, operaciones sobre archivos, nuevas dependencias ni modificaciones al extractor.
Se añade el test `AuriaVisualDefaultsTest` para confirmar el modo inicial y las alternativas conservadas.
Pendiente: compilación Android, unit tests, lint, pruebas en dispositivo (incluidos TalkBack, texto grande y animaciones reducidas).

## Fuera de alcance
La Fase 4 de favoritos con copia/movimiento de archivos por lotes requiere flujos separados, permisos SAF y pruebas de integridad para evitar pérdida de archivos. No está implementada en esta fase.
