# HappyBDay - Birthday Reminder & WhatsApp Sender 🎂📱
HappyBDay is a native Android app that helps you never forget the birthdays of the people you love again. The app lets you save details about the people you want to celebrate, schedule exact alarms, and at the chosen moment it sends you a notification. With a simple tap on the notification, you're automatically redirected to WhatsApp with your personalized message ready to be sent!
## ✨ Main Features
* **Birthday Management (CRUD):** Add, edit, and view people (Name, Phone, Date, Time, custom message).
* **Media Attachments:** Ability to attach a photo or video (from the gallery) associated with the message.
* **Filtering:** Easily filter the list of celebrants by month of the year.
* **Scheduled Notifications:** Uses `AlarmManager` to trigger local notifications (Push Notifications) at the exact date and time set.
* **WhatsApp Integration (Direct Message):** When the notification is tapped, the app uses an `Intent` to automatically open WhatsApp, pre-filling the phone number and the predefined text message.
* **Offline Local Storage:** All data is securely saved directly on the phone, using the `SQLite` database.
## 🛠️ Tech Stack & Architecture
* **Language:** [Kotlin](https://kotlinlang.org/)
* **Platform:** Android (Min SDK: 26 | Target SDK: 36)
* **UI:** XML, `RecyclerView`, Material Design
* **Database:** `SQLite` (via `SQLiteOpenHelper`)
* **System components:**
  * `BroadcastReceiver` (for listening to alarms)
  * `AlarmManager` (for exact scheduling on Android 12+)
  * `NotificationManager` (for displaying alerts)
  * Implicit Intents (for the WhatsApp API URL)
* **Permissions used:** `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `SEND_SMS`.
