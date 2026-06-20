# Splash Screen

## Purpose

Branded cold-start experience that replaces the system-default white flash. Displays the EnfocaAPP bison logo with scale animation and brand text for 2s before auto-transitioning to onboarding. Back press exits the app.

## Requirements

### Requirement: Dark Background

The splash background MUST render `#FF131313` full-bleed.

#### Scenario: Full-bleed dark background

- GIVEN the splash screen composable mounts
- WHEN the initial frame renders
- THEN the entire screen MUST show `#FF131313`
- AND `android:windowBackground` in `themes.xml` MUST be `#FF131313`

#### Scenario: No white flash on cold start

- GIVEN the app process is cold-started
- WHEN the Activity window is created
- THEN the window background MUST show `#FF131313` before any Compose rendering
- AND no system-default white or shield-icon flash MUST be visible

### Requirement: Bison Logo Display

The bison image MUST display at bottom-center of the screen.

#### Scenario: Logo renders at bottom-center

- GIVEN the splash screen is displayed
- WHEN the composable measures
- THEN `Image(bison.png)` MUST render at bottom-center
- AND the image MUST preserve its intrinsic aspect ratio

### Requirement: Scale Animation

The bison image MUST animate in with a scale-up from 0.8f to 1.0f over approximately 500ms.

#### Scenario: Scale animation plays on entry

- GIVEN the splash composable enters composition
- WHEN the first frame renders
- THEN the bison image MUST start at 0.8f scale
- AND MUST animate to 1.0f scale over approximately 500ms with an easing curve

### Requirement: Brand Text

"ztrene studios" MUST appear directly below the bison image.

#### Scenario: Brand text renders below logo

- GIVEN the splash screen is displayed
- WHEN the bison image renders
- THEN the text "ztrene studios" MUST appear directly below the image
- AND the text MUST use medium weight, small size, and subtle gray color
- AND appropriate spacing MUST exist between image and text

### Requirement: Display Duration

The splash MUST be visible for 2s total before auto-transition.

#### Scenario: Auto-navigate after 2 seconds

- GIVEN the splash screen is displayed
- WHEN 2 seconds elapse
- THEN the app MUST navigate to `OnboardingWelcomeScreen`
- AND the splash MUST be removed from the backstack (`popUpTo(Splash, inclusive=true)`)

### Requirement: Back Press Exit

Pressing Back during the splash MUST exit the app entirely.

#### Scenario: Back press exits app

- GIVEN the splash screen is displayed
- WHEN the user presses the system Back button
- THEN `finishAffinity()` MUST be called
- AND all activities MUST be finished (app exits to home screen)

### Requirement: Edge-to-Edge Rendering

The splash MUST respect `enableEdgeToEdge()` with no system bar interference.

#### Scenario: Full-bleed under system bars

- GIVEN `enableEdgeToEdge()` is active
- WHEN the splash renders
- THEN the background MUST extend behind the status and navigation bars
- AND the bison logo and brand text MUST be positioned above the navigation bar cutout
- AND content MUST NOT be clipped by system bars
