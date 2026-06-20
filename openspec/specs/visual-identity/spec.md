# Visual Identity

## Requirements

### Requirement: App Name String → "EnfocaApp"

The application name MUST display as "EnfocaApp" in the launcher and system settings.

(Rebrand from "Dopamina Guard")

### Requirement: Color Variable Prefix → "Enfoca"

All color variables MUST use the "Enfoca" prefix for brand consistency.

(Rebrand from "Dopamina" prefix)

### Requirement: Code String Literals → "EnfocaApp"

All hardcoded string references in Kotlin source MUST use "EnfocaApp" instead of "Dopamina Guard".

(Remove legacy brand from source)

### Requirement: Welcome Screen Logo

The welcome screen logo MUST display at 128dp.

#### Scenario: Logo renders at new size

- GIVEN the user is on the welcome screen
- WHEN `OnboardingWelcomeScreen` composable renders
- THEN the logo MUST use `Modifier.size(128.dp)` and remain centered

#### Scenario: Logo at minimum density (mdpi)

- GIVEN a device at 160 dpi
- WHEN the welcome screen renders
- THEN the logo MUST NOT appear pixelated (asset MUST provide ≥ 192×192 px)

### Requirement: Scanner Ring Around Logo

The scanning visualization MUST render as a standalone ring outside the logo, replacing the previous inside-hub overlay.

#### Scenario: Ring renders around logo

- GIVEN the welcome screen is rendered with scanning active
- WHEN the composable layout measures
- THEN a circular ring MUST surround the logo
- AND the ring MUST NOT clip or overlap the logo content

### Requirement: Description in Styled Card

The welcome description subtext MUST be wrapped in a card with padding, rounded corners, and contrasting background.

#### Scenario: Card renders with styling

- GIVEN the welcome screen is rendered
- WHEN the description text appears
- THEN it MUST be inside a `Surface` with `RoundedCornerShape`
- AND padding MUST be consistent regardless of text length

### Requirement: Custom Launcher Icon

The launcher icon MUST use the custom logo in adaptive-icon format.

#### Scenario: Icon shows branding logo

- GIVEN the app is installed
- WHEN the user views the launcher or recent apps
- THEN the icon MUST display the custom branding logo (not the Android default)
- AND the `<inset>` drawable MUST use 25% safe zone padding

#### Scenario: Icon at different densities

- GIVEN the app is viewed on various screen densities
- WHEN the launcher renders the adaptive icon
- THEN the logo MUST NOT be clipped by the safe zone
- AND the background color MUST match the brand color

### Requirement: Build Integrity

All renamed identifiers and strings MUST compile without errors.

#### Scenario: Clean build passes

- GIVEN all source files have been modified
- WHEN `./gradlew assembleDebug` runs
- THEN the build MUST succeed
- AND there MUST be zero references to "Dopamina" in Kotlin or resource files
