Android App (Jetpack Compose + Firebase)
BirthdayList is an Android application built with Kotlin and Jetpack Compose that helps users keep track of their friends’ birthdays.
The app uses:

Firebase Authentication → for login & registration

REST API (teacher‑provided) → for storing, retrieving, updating, and deleting birthday data

TECH STACK
- Frontend
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- AndroidX ViewModel

BACKEND
- Firebase Authentication (email/password)
- Custom REST API (provided by teacher)
GET birthdays
POST new birthday
PUT update birthday
DELETE birthday

Dependency Injection
Koin (AppModule.kt)


APP FEATURES
- Create account & login using Firebase
- Fetch birthdays from REST API
- Add new birthdays
- View next upcoming birthday
- View full list of birthdays
- (Optional) Edit or delete birthdays
- Clean MVVM architecture
