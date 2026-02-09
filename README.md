# DramWise

DramWise is an Android application developed to support the monitoring of alcohol consumption through structured data entry and automated alcohol unit calculation. The application allows users to record drinks, view summaries of consumption, and reflect on drinking patterns over time.

This project was created as part of an undergraduate computing programme and focuses on usability, local data persistence, and extensible application design.

## Features

- Record drinks with volume (ml) and alcohol by volume (ABV)
- Automatic calculation of UK alcohol units
- Daily consumption summary
- Historical record of all entries
- Weekly insights into alcohol consumption

## Alcohol Unit Calculation

Alcohol units are calculated using the UK standard formula:

  - Units = (Volume in ml × ABV %) / 1000

Example:
  - 500 ml beer at 5% ABV
  - Units = (500 × 5) / 1000 = 2.5 units

## Technology Used

- Language: Java
- Platform: Android
- Database: Room (SQLite)
- UI: Material Design components
- Navigation: Android Navigation Component

## Running the App

1. Clone the repository: git clone https://github.com/YOUR_USERNAME/dramwise-android.git 
2. Open the project in Android Studio
3. Ensure:
   - Android SDK version 26 or higher
   - Gradle sync completes successfully
4. Run on an Android emulator or physical Android device

## Academic Context

This application was developed as part of an undergraduate computing project exploring digital tools for alcohol monitoring and personalised health feedback.

## License

This project is for educational purposes.
