# HealthMate – Personal Health & Wellness Tracker

## 1. Project Overview

HealthMate is an Android-based Personal Health and Wellness Tracker developed using Kotlin and SQLite. The application helps users record, monitor, and manage their daily health and wellness activities through a user-friendly dashboard.

## 2. Project Objectives

- To help users monitor their daily health activities.
- To maintain personal health records.
- To provide customised daily health goals.
- To display health progress through visual charts.
- To store health information locally using SQLite.

## 3. Main Features

- **Daily Steps Tracking:** Record daily steps.
- **Calories Tracking:** Monitor calories burned.
- **Water Intake Tracking:** Record daily water consumption.
- **Heart Rate Tracking:** Store heart rate readings.
- **Sleep Tracking:** Record daily sleep duration.
- **Weight Tracking:** Maintain body weight records.
- **Weekly Activity Analytics:** View weekly activity trends.
- **Custom Daily Goals:** Set personal health targets.
- **Health History:** View previously saved health records.
- **CRUD Operations:** Add, view, update and delete health records.
- **Search and Filter:** Find health records by date.

*Note: Health measurements are manually entered by users.*

## 4. Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Application development |
| XML | User interface design |
| SQLite | Local database |
| Android Studio | Development environment |
| MVVM | Application architecture |
| LiveData | UI data observation |
| RecyclerView | Displaying health records |
| Git & GitHub | Version control |

## 5. Application Architecture

HealthMate follows the Model-View-ViewModel (MVVM) architecture.

- **Model:** Represents health records and daily goals.
- **View:** Android activities and XML layouts.
- **ViewModel:** Manages UI-related data and application logic.
- **Repository:** Handles communication between ViewModel and database.
- **Database:** SQLite database for local data storage.

## 6. Database

The application uses SQLite for local data management.

Main database tables:
- `health_records`
- `daily_goals`

The database supports CRUD operations and preserves saved information locally.

## 7. Installation

1. Clone or download this repository.
2. Open the project using Android Studio.
3. Allow Gradle synchronisation to complete.
4. Connect an Android device or start an emulator.
5. Build and run the application.

## 8. Project Information

**Project Name:** HealthMate – Personal Health & Wellness Tracker

**Platform:** Android

**Programming Language:** Kotlin

**Database:** SQLite

**Development Environment:** Android Studio

## 9. Developer

**Developer:** Jameel Muhammed Thazki

**GitHub Repository:** https://github.com/ahmedthazki/Health-Mate
