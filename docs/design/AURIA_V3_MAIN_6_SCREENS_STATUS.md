# AURIA V3 — Main: six-screen visual migration

## Status (2026-10-30, Peru)

All approved V2 and V3 source changes have been merged into `main`.
Further changes to both platforms and the Android home-studio layout
were committed **directly to main**. Branch names are historical and
should not be confused with active unpublished work.

The design is visually differentiated, but this document does **not**
declare the application build/release ready.

## Identity

- Original broken Halo icon and orbital lines; not old source-project art.
- Flow: asymmetric, roomy, editorial movement.
- Nebula: round/orbit-focused atmosphere.
- Pulse: dense and rhythmic tiles.
- Studio: precision-control layout.
- Four modes select effective Android home, navigation and miniplayer
  styles. Previous preferences remain stored and may be restored if
  the selected palette is no longer an AURIA mode.
- Legacy Android Home Studio presets no longer appear as effective
  choices while an AURIA mode is selected. Section customization now
  edits the **effective** mode-specific layout.

## Six active surfaces

| Surface | Android | Windows desktop |
|---|---|---|
| Inicio / Home | Halo-mode hero, effective style layout, wave/branding | Orbital album hero, Halo heading, branded sidebar |
| Biblioteca / Library | Mode-specific tiles, colors, borders and Halo marker | Distinct panel shapes and Halo library heading |
| Descubrir / Discover | Halo hero, asymmetrical category cards | Editorial gradients, asymmetrical cards, Halo heading |
| Reproductor / Player | Halo source header, mode-aware visuals | Orbital artwork, mode-specific geometry, Halo marker |
| DJ | AURIA DJ controls and fixed signal ribbon | AURIA DJ mix signal ribbon for transitions |
| Ajustes / Appearance | AURIA four-mode selector, preview thumbnails and brand | Halo settings heading and distinct surfaces |

These are front-end layout/style improvements; they do not claim
to implement automatic musical key/BPM detection or a new mixing
engine.

## Preservation guarantees

Do not delete, overwrite or migrate users' playlists, liked songs,
offline files, device libraries, or application databases during a
visual-only phase. Keep existing internal Kotlin packages for binary
compatibility until a dedicated tested migration can be planned.
Do not silently change Media3, VLC or download logic to address
appearance.

## Release acceptance checklist (pending)

1. Android: Gradle compile, unit tests, UI layout tests on narrow and
   large screens, dark/light, landscape, accessibility font scaling.
2. Test selecting every visual mode and verify matching Home, Library,
   Discover, Player, DJ and Appearance after app restart.
3. Test library search, playlists, physical favorite-file operations,
   offline playback, stream playback, video and queue persistence.
4. Windows: compile and launch installer; verify navigation, artwork,
   localized titles, library index and DJ transition view.
5. Audit user-facing text and images for historical brand residue. Old
   internal class names alone are not a visual similarity.
6. Check Github Actions infrastructure: preflight and compile-related
   jobs have failed almost immediately and downstream jobs have been
   skipped. Do not treat skipped jobs as passing tests.

## Known remaining product work

- Full live UI inspection and visual regression testing have **not**
  been completed in this session; no APK/installer validation.
- The physical **Me gusta** transfer flow and deeper DJ automation are
  independent functional milestones, not completed by this design pass.
- Some secondary screens and custom theme previews still use older
  shared widgets; continue auditing them based on actual screenshots.
