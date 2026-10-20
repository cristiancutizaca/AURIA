# AURIA 🎧

**AURIA** es un reproductor de música para **Android y Windows**, con biblioteca local, música online y un modo DJ automático en desarrollo.

## Descargar e instalar

- [Versiones y archivos publicados por AURIA](https://github.com/cristiancutizaca/AURIA/releases)
- [Compilaciones de Android y Windows](https://github.com/cristiancutizaca/AURIA/actions)
- [Código fuente](https://github.com/cristiancutizaca/AURIA)

> **Aviso:** solo instala paquetes publicados en este repositorio por AURIA. No son actualizaciones de Levyra ni usan su canal de instalación. Es posible que aún no haya una versión estable compilada.

### Android

El identificador de aplicación de AURIA es `com.cristiancutizaca.auria`. Esto permite instalarla por separado de la aplicación de origen. Las versiones de depuración agregan el sufijo `.debug`.

Para crear una APK de prueba, instala Android SDK y JDK, y ejecuta:

```bash
./gradlew :app:assembleDebug
```

La APK estará en `app/build/outputs/apk/debug/`. Las APK de producción requieren **una clave de firma propia**, sin reutilizar la firma del proyecto anterior.

### Windows

AURIA utiliza su propio nombre de instalador, acceso directo, protocolo `auria://`, identidad MSI y carpeta de datos `%APPDATA%\AURIA`.

Desde `desktop/`, con JDK 21 y las dependencias indicadas en [la guía de escritorio](desktop/README.md):

```bash
./gradlew check
./gradlew packageReleaseMsi packageReleaseExe
```

El empaquetado requiere Windows y las herramientas de instalación correspondientes. Las compilaciones antiguas de Levyra no son actualizaciones de AURIA.

## Funcionalidades

- Reproducción de música local y online.
- Biblioteca, favoritos, playlists, letras y descargas.
- Recomendaciones personalizadas a partir de la escucha.
- **AURIA DJ** con reproducción continua, preparación de la siguiente canción y superposición prolongada.
- Reproductor de escritorio y aplicación Android.

**Estado del DJ:** la superposición de pistas está implementada en código. La sincronización automática por BPM, la selección de puntos musicales de entrada y la separación de voces/instrumentos siguen pendientes de validación e implementación; no se debe presentar como una mezcla profesional ya terminada.

## Actualizaciones, anuncios y financiación

Las direcciones de actualizaciones se limitan al repositorio `cristiancutizaca/AURIA`. En Android, la instalación automática de actualizaciones está **desactivada** hasta tener un canal y una clave de firma propios. Las notificaciones promocionales heredadas están desactivadas y AURIA no incorpora enlaces de donación del autor del proyecto de origen.

No están habilitados sistemas de donaciones ni pagos dentro de AURIA.

## Origen y licencias

AURIA se basa en el proyecto de código abierto [Levyra-deepsound](https://github.com/LUC4N3X/Levyra-deepsound). Agradecemos a sus autores y colaboradores por el trabajo original. AURIA es una modificación independiente, **no es una versión oficial ni está afiliada a los mantenedores de Levyra**.

Se preservan el archivo [LICENSE](LICENSE), los avisos de terceros y las atribuciones exigidas por las licencias correspondientes. Los nombres de paquetes Kotlin, algunos módulos internos, APIs y rutas de compatibilidad pueden conservar la denominación técnica heredada para evitar romper funcionalidades; eso no implica que AURIA use los servicios oficiales del proyecto de origen.

La música y los servicios externos siguen sujetos a sus respectivas condiciones y derechos.
