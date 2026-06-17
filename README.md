# HappyBDay - Birthday Reminder & WhatsApp Sender 🎂📱

HappyBDay este o aplicație nativă Android care te ajută să nu mai uiți niciodată de zilele de naștere ale celor dragi. Aplicația îți permite să salvezi detalii despre sărbătoriți, să programezi alarme exacte și, la momentul ales, îți trimite o notificare. Printr-o simplă apăsare pe notificare, ești redirecționat automat pe WhatsApp cu mesajul tău personalizat gata de a fi trimis!

## ✨ Funcționalități Principale (Features)
* **Gestionare Zile de Naștere (CRUD):** Adaugă, editează și vizualizează persoanele (Nume, Telefon, Dată, Oră, Mesaj personalizat).
* **Atașamente Media:** Posibilitatea de a atașa o poză sau un video (din galerie) asociat mesajului.
* **Filtrare:** Filtrează ușor lista de sărbătoriți în funcție de luna anului.
* **Notificări Programate:** Folosește `AlarmManager` pentru a declanșa notificări locale (Push Notifications) exact la ora și data setată.
* **Integrare WhatsApp (Direct Message):** La apăsarea notificării, aplicația folosește un `Intent` pentru a deschide automat WhatsApp, completând numărul de telefon și mesajul text predefinit.
* **Stocare Locală offline:** Toate datele sunt salvate sigur, direct pe telefon, folosind baza de date `SQLite`.

## 🛠️ Tehnologii și Arhitectură (Tech Stack)
* **Limbaj:** [Kotlin](https://kotlinlang.org/)
* **Platformă:** Android (Min SDK: 26 | Target SDK: 36)
* **UI:** XML, `RecyclerView`, Material Design
* **Bază de date:** `SQLite` (prin `SQLiteOpenHelper`)
* **Componente de sistem:**
  * `BroadcastReceiver` (pentru ascultarea alarmelor)
  * `AlarmManager` (pentru programarea exactă pe Android 12+)
  * `NotificationManager` (pentru afișarea alertelor)
  * Implicit Intents (pentru URL-ul API-ului WhatsApp)
* **Permisiuni folosite:** `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `SEND_SMS`.
