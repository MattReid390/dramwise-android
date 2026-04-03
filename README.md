# DramWise

An Android application for tracking alcohol consumption against NHS guidelines. Log drinks, monitor daily and weekly unit totals, and get personalised smart feedback on your drinking patterns.

Built as an Honours Project for an undergraduate computing programme at Glasgow Caledonian University.

---

## Screenshots

> _Add screenshots of the Dashboard, Add Drink, History, Insights, and Settings screens here._

---

## Features

- **Dashboard** — today's drink count and unit total, with a weekly progress bar tracking against your personal limit
- **Add a Drink** — quick-select from a categorised list of pre-defined drinks (Beers, Wines, Spirits, Cocktails) or enter a custom volume and ABV; live unit preview before saving
- **History** — scrollable log of every entry with drink name, volume, ABV, timestamp, and unit count; one-tap clear with confirmation
- **Insights** — bar chart of the last 7 days, weekly total and daily average, risk level badge (Low / Moderate / High), and rule-based smart feedback
- **Settings** — adjustable weekly unit limit (1–30 units) persisted across sessions; clear all data with a single tap

---

## Unit Calculation

Alcohol units are calculated using the standard UK formula:

```
Units = (Volume ml × ABV%) / 1000
```

**Example** — 500 ml beer at 5% ABV → `(500 × 5) / 1000 = 2.5 units`

---

## Architecture

| Layer | Implementation |
|---|---|
| Language | Java |
| Min SDK | 26 (Android 8.0) · Target SDK 36 |
| UI | Material Design 3 (`Theme.Material3.DayNight.NoActionBar`) |
| Navigation | Android Navigation Component (bottom nav, single-activity) |
| Architecture | MVVM — `AndroidViewModel` + `LiveData` |
| Database | Room (SQLite) with `DrinkEntry` and `DrinkType` tables |
| Chart | MPAndroidChart v3.1.0 (`BarChart`) |
| Persistence | `SharedPreferences` for weekly limit |
| Testing | JUnit 4 unit tests (`UnitsCalculator`, `DateUtil`) |
| Build | Gradle Kotlin DSL · version catalog (`libs.versions.toml`) |

### Project structure

```
app/src/main/java/org/me/gcu/dramwise/
├── MainActivity.java
├── data/
│   ├── AppDatabase.java          # Room database, migrations
│   ├── DrinkDao.java             # Entry queries
│   ├── DrinkTypeDao.java         # Drink type + category queries
│   ├── DrinkEntry.java           # @Entity — logged drink
│   ├── DrinkType.java            # @Entity — pre-defined drink
│   ├── DailyUnits.java           # Query result projection
│   └── DrinkRepository.java      # Single source of truth (Singleton)
└── ui/
    ├── dashboard/
    │   ├── DashboardFragment.java
    │   └── DashboardViewModel.java
    ├── add/
    │   ├── AddDrinkFragment.java
    │   └── SectionedDrinkAdapter.java
    ├── history/
    │   ├── HistoryFragment.java
    │   ├── HistoryViewModel.java
    │   └── HistoryAdapter.java
    ├── insights/
    │   ├── InsightsFragment.java
    │   └── InsightsViewModel.java
    └── settings/
        └── SettingsFragment.java

app/src/test/java/org/me/gcu/dramwise/util/
├── UnitsCalculatorTest.java      # 13 unit calculation tests
└── DateUtilTest.java             # 14 date utility tests
```

---

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Hedgehog or later recommended)
- Android SDK 26+
- An Android emulator or physical device running Android 8.0+

### Setup

```bash
git clone https://github.com/YOUR_USERNAME/dramwise-android.git
cd dramwise-android
```

1. Open the project in Android Studio (`File → Open`)
2. Wait for Gradle sync to complete
3. Run on an emulator or device (`Shift + F10`)

### Running tests

```bash
./gradlew test
```

---

## NHS Guidelines

The app uses the NHS recommended limit of **14 units per week** as its default threshold. This can be adjusted in the Settings screen. For more information visit [nhs.uk/live-well/alcohol-advice](https://www.nhs.uk/live-well/alcohol-advice/alcohol-units/).

---

## License

This project is submitted for academic assessment. You are welcome to read and reference the code, but please do not copy or redistribute it for your own academic submissions.
