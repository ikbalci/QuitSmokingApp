# <img src="app/src/main/res/drawable/smoke.png" alt="Quit Smoking App Icon" width="50" height="50"> Quit Smoking App 

Welcome to the Quit Smoking App! This app helps you quit smoking by tracking your progress and showing your achievements.

## Features

- **Timer**: Tracks how long it has been since you quit smoking.
- <img src="app/src/main/res/drawable/money.png" alt="Money Saved Icon" width="20" height="20"> **Money Saved**: Shows how much money you have saved based on your smoking habits.
- <img src="app/src/main/res/drawable/cigarette.png" alt="Cigarettes Avoided Icon" width="20" height="20"> **Cigarettes Avoided**: Shows the number of cigarettes you haven't smoked since quitting.
- <img src="app/src/main/res/drawable/calendar.png" alt="Days Quit Icon" width="20" height="20"> **Days Quit**: Tracks the number of days since you quit smoking.
- **Advertisements**: Displays ads to support app development.

## Screenshots

<img src="app/src/main/res/drawable/main_screen.png" alt="Main Screen" width="300">
<img src="app/src/main/res/drawable/custom_dialog.png" alt="Custom Dialog" width="300">

## Usage

1. **Start the App**: On the first launch, enter your smoking habits (cigarettes per day and cost per pack).
2. **Start Timer**: Click the "Start Timer" button to begin tracking your quit journey.
3. **View Progress**: See your progress, including time quit, money saved, cigarettes avoided, and days quit.
4. **Stop Timer**: To stop the timer, click the "Stop Timer" button and confirm.

## Development

### Code Structure

- `MainActivity.java`: Main activity with the core functionality.
- `activity_main.xml`: Layout file for the main screen.
- `custom_dialog.xml`: Layout file for the initial input dialog.

### Ad Integration

The app uses Google AdMob for ads. Ensure you have the following dependencies in your `build.gradle` file:

```groovy
dependencies {
    implementation 'com.google.android.gms:play-services-ads:20.6.0'
}
