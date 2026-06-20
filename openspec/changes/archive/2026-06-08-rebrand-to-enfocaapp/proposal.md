# Proposal: Rebrand to EnfocaApp

## Intent

Rename "Dopamina Guard" → "EnfocaApp" across all user-facing surfaces and internal code identifiers. Refresh onboarding welcome screen visuals (logo size, scanner, description card) and replace the default Android icon with the custom logo.

## Scope

### In Scope
- Rename strings: `strings.xml` (3), hardcoded Kotlin in `OnboardingContractScreen.kt` (2), `StatsScreen.kt` (1)
- Refactor 43 `Dopamina*` color var usages in `Color.kt`, `Theme.kt`, `OnboardingContractScreen.kt`, `StatsScreen.kt`
- Welcome screen: logo 64→128dp, scanner→ring, description→card
- App icon: replace foreground with `logo.png`, update background color
- Update `AGENTS.md` identity line
- Multi-agent apply execution

### Out of Scope
- Other `.md` docs, functional changes, DataStore fixes, icon redesign

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- None — rename + visual refresh only, no spec-level behavior changes

## Approach

**Rename**: Atomic find-replace in `strings.xml` + Kotlin files. Refactor `Dopamina*`→`Enfoca*` in `Color.kt` and propagate across 4 files.

**Welcome screen**: Logo `Modifier.size(128.dp)`. Extract `ScanningOverlay` from inside hub to a standalone ring. Wrap description Text in `Surface` with `RoundedCornerShape`, padding, contrasting bg.

**App icon**: Replace `ic_launcher_foreground.xml` with `<inset>` drawable wrapping `logo.png` (adaptive-icon safe zone). Change background color to branding.

**Parallel apply**: 3 agents — (a) rename/refactor, (b) welcome UI, (c) app icon.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `res/values/strings.xml` | Modified | 3 string values |
| `ui/theme/Color.kt` | Modified | 10 vars renamed |
| `ui/theme/Theme.kt` | Modified | 8 refs updated |
| `OnboardingWelcomeScreen.kt` | Modified | Logo, scanner, card |
| `OnboardingContractScreen.kt` | Modified | 2 strings + 4 color refs |
| `StatsScreen.kt` | Modified | 1 string + 16 color refs |
| `ic_launcher_foreground.xml` | Modified | Logo drawable |
| `ic_launcher_background.xml` | Modified | Brand color |
| `AGENTS.md` | Modified | Identity line |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Missed "Dopamina" ref | Low | grep + build check before commit |
| Color rename breaks layout | Low | Compose previews per screen |
| Scanner ring visual glitch | Med | Emulator verify before finalize |
| Adaptive icon clipping logo | Med | Use `<inset>` with 25% padding |

## Rollback Plan

File-level revert per modified file. Full revert: `git checkout main && git branch -D rebrand-to-enfocaapp`.

## Dependencies

- None. Android SDK 36, no external APIs.

## Success Criteria

- [ ] `app_name` shows "EnfocaApp" in launcher and settings
- [ ] No user-facing "Dopamina" or "Dopamine" text
- [ ] Logo at 128dp, scanner as ring, description in styled card
- [ ] App icon shows custom logo, not Android robot
- [ ] `./gradlew assembleDebug` succeeds
