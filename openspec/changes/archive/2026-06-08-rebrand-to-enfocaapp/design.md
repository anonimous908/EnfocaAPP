# Design: Rebrand to EnfocaApp

## Technical Approach

5 independent workstreams: (1) rename 3 strings + 3 hardcoded references, (2) refactor 43 `Dopamina*`→`Enfoca*` color identifiers across 5 files, (3) welcome screen visual refresh (logo 64→128dp, sweeping bar→rotating scanner ring, description→card), (4) adaptive icon foreground (logo.png via `<inset>`) + brand background, (5) AGENTS.md identity line. Zero behavioral change — all static text/asset/UI work.

## Architecture Decisions

| Decision | Choice | Alternatives | Rationale |
|----------|--------|-------------|-----------|
| Scanner ring approach | Rotating arc via `Canvas` + `drawArc` + `rememberInfiniteTransition` | Keep vertical sweep bar; full border with animated dash | Arc rotation preserves "scanning" metaphor, matches circular hub language, reuses existing `infiniteTransition` pattern. Cheaper than `PathMeasure` dash animation |
| Pulsing rings with larger logo | Base size 64→160dp, hub 96→128dp | Keep rings at 64dp (visually dwarfed) | Proper visual hierarchy: rings pulse outside logo → scanner ring at hub perimeter → logo centered |
| Adaptive icon format | `<inset drawable="@drawable/logo" android:inset="25%" />` | Convert logo to vector; use `foregroundLayer` | `<inset>` respects 66% safe zone without PNG conversion. Logo has gradients unsuitable for vector |
| Background icon color | Solid `#131313` (app bg) | `#0052FF` (primary), green grid (current) | Near-black provides max contrast against logo, matches app's dark theme identity |
| Color rename in StatsScreen | Wildcard `import *.theme.*` used — no import change needed | None | Wildcard import auto-resolves renamed vars. Only verify build succeeds |

## Data Flow

No runtime data flow changes — all changes are static text/asset/UI composition. Zero behavioral impact.

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `res/values/strings.xml` | Modify | 3 values: `app_name`, `onboarding_info_descripcion`, `onboarding_contract_text_2` |
| `OnboardingContractScreen.kt` | Modify | 2 hardcoded strings (privacy/terms dialogs) + 4 color imports+usages → Enfoca* |
| `StatsScreen.kt` | Modify | 1 hardcoded string (line 66) + 16 color refs via wildcard import |
| `ui/theme/Color.kt` | Modify | Comment + 10 vars: `Dopamina*`→`Enfoca*` |
| `ui/theme/Theme.kt` | Modify | 8 references in `DarkColorScheme` |
| `OnboardingInfoScreen.kt` | Modify | 2 unused imports → Enfoca* |
| `OnboardingWelcomeScreen.kt` | Modify | Logo 64→128dp, hub 96→128dp, rings 64→160dp, ScanningOverlay→ScannerRing, description→Surface card |
| `drawable/ic_launcher_foreground.xml` | Replace | Vector path→`<inset>` drawable wrapping `@drawable/logo` |
| `drawable/ic_launcher_background.xml` | Replace | Green grid→solid `#FF131313` |
| `AGENTS.md` | Modify | Lines 1 + 5: "Dopamina Guard"→"EnfocaApp" |

## Compose Component Changes (Welcome Screen)

```
Box(256dp)                        ← same size
  ├── crosshairs                  ← unchanged
  ├── PulsingRing × 3             ← baseSize: 64→160dp, scale 0.8→1.5 (unchanged)
  ├── Box(128dp) ← hub            ← 96→128dp
  │   └── Image(logo, 128dp)      ← 64→128dp
  └── ScannerRing(144dp)          ← NEW: rotating arc overlapping hub edge
```

- **ScannerRing** (new private composable): `Canvas(size=144dp)` draws a 120° arc with `Brush.sweepGradient` from transparent→primary→transparent. Arc rotates 0→360° via `rememberInfiniteTransition` + `animateFloat` with `tween(3000ms, LinearEasing)`. `StrokeCap.Round`, stroke width 3dp.
- **PulsingRing**: Add `baseSize: Dp = 160.dp` parameter. All `Modifier.size(...)` references use it.
- **Description** → **Card**: Wrap `Text` in `Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant)` with `Modifier.padding(16.dp)` and `fillMaxWidth()`.

## Interfaces / Contracts

No new types. `ScannerRing` is private within `OnboardingWelcomeScreen.kt`, same scope as existing `PulsingRing`.

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Build | Compilation after rename | `./gradlew assembleDebug` |
| Visual | Icon shows logo, not robot | Emulator launcher check |
| Visual | Scanner ring animates, description in card | Compose Preview + emulator |
| Regression | Onboarding flow unaffected | Manual smoke-test pager |

## Migration / Rollout

No migration required. All static assets. Icon takes effect on next install/launcher reload.

## Dependency Graph (Multi-Agent)

```
Agent A (rename/refactor) ───────────────────────────────
  ├── strings.xml + OnboardingContractScreen.kt (2 strings)
  ├── StatsScreen.kt (1 string)
  ├── Color.kt + Theme.kt + OnboardingContractScreen.kt (4 color)
  ├── OnboardingInfoScreen.kt (2 unused imports)
  ├── StatsScreen.kt (16 color refs, wildcard import)
  └── AGENTS.md (identity line)

Agent B (welcome UI) ────────────────────────────────────
  └── OnboardingWelcomeScreen.kt only

Agent C (icon) ──────────────────────────────────────────
  ├── ic_launcher_foreground.xml
  └── ic_launcher_background.xml
```

**Dependency edges**: NONE — all 3 agents touch disjoint file sets. Full parallel execution safe. Agent A must handle both string + color changes in `OnboardingContractScreen.kt` and `StatsScreen.kt` to avoid cross-agent file conflicts.

## Open Questions

- [ ] Scanner ring sweep angle: 120° recommended (visible arc without looking like full loading spinner). Confirm or adjust.
- [ ] Brand background color for icon: `#FF131313` selected. Alternative `#FF0052FF` (primary blue) if logo has dark elements that don't contrast against near-black.
