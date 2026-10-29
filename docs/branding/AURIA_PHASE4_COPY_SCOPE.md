# AURIA fase 4: copia segura de favoritos locales

Se añadió **Copiar Me gusta** en el detalle de favoritos. Permite seleccionar canciones existentes mediante pulsación larga y abrir el selector de carpetas Android (SAF). Para la lista completa, el botón copia los favoritos locales detectados. No crea nuevos favoritos, no borra archivos originales y no requiere permiso de almacenamiento completo.

- Entrada: Track.source == "Offline" y content:// de MediaStore.
- Destino: carpeta elegida mediante ACTION_OPEN_DOCUMENT_TREE.
- Duplicados: se omiten si coinciden por nombre de archivo en la carpeta. No se sobrescriben.
- Transferencia: IO, buffer 64 KB, cancelación cooperativa, validación de longitud, eliminación de archivos parciales al fallar.
- Resultado: informe de copiados, existentes, no locales y errores.
- Sin migraciones de BD ni transferencias de pistas de servicios online.

**Pendiente:** mover físicamente (necesita gestionar las autorizaciones de escritura/borrado de MediaStore), autoguardado opcional, persistencia de carpeta preferida, reanudación de lotes tras cierre de proceso y sincronización Windows. No anunciar estos puntos como completados.

**Validación:** pendiente de build, test en teléfono y comprobación de proveedores SAF. Es un avance funcional de fase 4, no cierre completo de la fase.
