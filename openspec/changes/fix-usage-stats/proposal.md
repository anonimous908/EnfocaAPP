# Proposal: Fix Usage Stats Data Accuracy

## Intent

RealityRevealScreen shows hours that "don't match 100%." Root cause: `getTodayUsageStats()` measures device screen state (SCREEN_INTERACTIVE) instead of actual app foreground time, inflates <1h to "1h", and forces min 1 unlock. Users see misleading data, undermining trust in EnfocaApp's core value.

## Scope

### In Scope
- Rewrite AppUsageRepository.getTodayUsageStats() with correct API
- Create `TodayUsageStats` data class (hours, minutes, unlocks)
- Update OnboardingViewModel state types from `Pair<Int,Int>` to typed class
- Display time as "Xh Ym" in RealityRevealScreen
- Remove both artificial inflation rules

### Out of Scope
- Other screens (StatsScreen, HomeScreen) — deferred
- Permission flow or grant UX changes
- DataStore persistence for usage snapshots
- Multi-day or historical aggregation

## Capabilities

### New Capabilities
- `usage-stats`: Usage statistics retrieval and data model (foreground time, unlocks)

### Modified Capabilities
None — existing specs (visual-identity, splash-screen) are unrelated.

## Approach

1. Replace `queryEvents()`/SCREEN_INTERACTIVE with `queryAndAggregateUsageStats()`. Sum `getTotalTimeInForeground()` across all packages for real usage time.
2. Keep `queryEvents()` ONLY for `KEYGUARD_HIDDEN` → unlock count.
3. Create `TodayUsageStats(hours: Int, minutes: Int, unlocks: Int)`.
4. Remove `if (totalHours == 0 …) 1` rounding and `if (unlocks == 0) 1` inflation.
5. Update RealityRevealScreen to accept minutes and display "Xh Ym".

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| core/repository/AppUsageRepository.kt | Modified | Rewrite getTodayUsageStats() core logic |
| core/model/TodayUsageStats.kt | New | Data class for typed usage stats |
| ui/…/onboarding/OnboardingViewModel.kt | Modified | State types → TodayUsageStats |
| ui/…/onboarding/RealityRevealScreen.kt | Modified | Accept minutes, display "Xh Ym" |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| queryAndAggregateUsageStats() empty on fresh devices | Med | Fallback to "—" or 0h 0m with info note |
| Permission denied → 0 values | Low | Already handled, show 0 + permission prompt |

## Rollback Plan

Revert AppUsageRepository.kt to original `queryEvents()` impl. Revert ViewModel state to `Pair<Int,Int>`. Revert screen to Int-only hours display.

## Dependencies

- minSdk 29 (`queryAndAggregateUsageStats` available since API 28)
- No new library dependencies

## Success Criteria

- [ ] `./gradlew test` passes
- [ ] RealityRevealScreen shows "Xh Ym" with accurate foreground time
- [ ] No artificial rounding inflates values
- [ ] Unlock count reflects actual device unlock events
