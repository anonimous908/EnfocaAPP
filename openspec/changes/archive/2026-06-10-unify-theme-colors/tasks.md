# Tasks: Unificar Colores del Tema

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~100 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | ask-on-risk |
| Chain strategy | pending |

Decision needed before apply: Yes
Chained PRs recommended: No
Chain strategy: pending
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Foundation + screens | PR 1 | Single PR — under 400 lines, all changes are tightly coupled |

## Phase 1: Foundation — Color Palette

- [x] 1.1 Add 11 `EnfocaLight*` color variables to `ui/theme/Color.kt` (Background, Surface, OnBackground, OnSurfaceVariant, Primary, PrimaryContainer, SurfaceContainer, SurfaceContainerHigh, SurfaceContainerLow, OutlineVariant, Outline)
- [x] 1.2 Remove 6 legacy variables (`Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`) from `Color.kt`; grep for references to verify none remain
- [x] 1.3 Set `dynamicColor = false` default in `EnfocaAPPTheme` (`Theme.kt`)
- [x] 1.4 Expand `DarkColorScheme` to all 16 slots: add `onPrimary` (`EnfocaBackground`), `secondary` (`EnfocaOnSurfaceVariant`), `onSecondary` (`EnfocaBackground`), `onSurface` (`EnfocaOnBackground`), `outline` (`EnfocaSurfaceContainerHighest`), `error` (`0xFFFFB4AB`), `onError` (`0xFF690005`)
- [x] 1.5 Create full `LightColorScheme` with 16 slots mapping `EnfocaLight*` + `Color.White` for onPrimary/onSecondary + `Color(0xFF001B3E)` for onPrimaryContainer + standard error colors

## Phase 2: Screen Refactors

- [x] 2.1 Replace ~25 `Color(0xFF...)` in `UsageEstimationScreen.kt` with `MaterialTheme.colorScheme.*` (background, glow, title, subtitle, card bg/border, hour number, "h" accent, labels, button container/content, footer, slider, sheet container/handle, pulse glow, icon circle bg/border/tint, sheet text, outlinedButton). Exempted: 4 feedback severity colors (`Monje`, `Promedio`, `Intensivo`, `Matrix`)
- [x] 2.2 Replace ~15 `Color(0xFF...)` in `RealityRevealScreen.kt` with `MaterialTheme.colorScheme.*` (background, subtitle muted, "pero pasas" text, "9h" accent, card bg/border, card labels, big number, "hoy" label, bar active/inactive, button container/content, CTA footer, badge bg/border/text)
- [x] 2.3 Replace `EnfocaSurfaceContainerHighest` / `EnfocaSurfaceContainerLow` in `OnboardingContractScreen.kt` with `MaterialTheme.colorScheme.surfaceContainerHighest` / `surfaceContainerLow`; remove direct imports from `Color.kt`

## Phase 3: Build & Verify

- [x] 3.1 Build: `./gradlew assembleDebug` — must compile without errors
- [x] 3.2 Run: `./gradlew test` — all tests must pass
- [x] 3.3 Verify no hardcoded `Color(0xFF` theme colors remain in refactored screens (grep modified files, excluding exempted semantic feedback colors)
