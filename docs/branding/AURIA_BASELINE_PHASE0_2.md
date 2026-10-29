# Registro de migración AURIA: fases 0 a 2

- Rama destino: `main`.
- Commit de partida: `9d25410d2622061a58f2a554f7fa4ff1a595d262`.
- Se preservan las operaciones Media3, Room, descargas, favoritos, listas, historial y usuarios.
- Las preferencias visuales preexistentes se respetan; los defaults solo se aplican a nuevas configuraciones.
- Iconografía adaptativa y monocroma nueva; iconos PNG de respaldo para lanzadores anteriores.
- No se añadieron servicios de cuenta, telemetría ni dependencias de terceros.
- No se eliminaron atribuciones legales al proyecto de origen.

## Verificaciones que aún se deben ejecutar en entorno Android
1. `python3 scripts/ai_quality_gate.py --profile fast`
2. `./gradlew :app:testDebugUnitTest`
3. `./gradlew :app:assembleDebug`
4. Abrir la app y probar reproducción, notificaciones, navegación, ajustes antiguos y colores.
5. Validar iconos adaptativos, animación reducida, contraste y fuentes grandes.

Estado: código y recursos enviados a revisión de la CI; las pruebas de Android no se ejecutaron en esta sesión.
