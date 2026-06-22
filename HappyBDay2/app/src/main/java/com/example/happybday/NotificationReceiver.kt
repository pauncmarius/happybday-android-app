package com.example.happybday

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import java.net.URLEncoder

//clasă Android care ascultă evenimente (alarme, SMS, boot, etc.) și reacționează când acestea apar.
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra("name") ?: "Sărbătorit"
        val phone = intent.getStringExtra("phone") ?: ""
        val message = intent.getStringExtra("message") ?: ""
        val mediaUriStr = intent.getStringExtra("mediaUri") ?: ""

        val cleanPhone = phone.replace("\\D".toRegex(), "")

        val whatsappIntent: Intent
        val notificationText: String

        if (mediaUriStr.isEmpty()) {
            // Caz doar text
            notificationText = "Apasă pentru a trimite mesajul pe WhatsApp."
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${URLEncoder.encode(message, "UTF-8")}"
            whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            notificationText = "Apasă pentru a trimite mesajul și fișierul media."

            val mediaUri = Uri.parse(mediaUriStr) // string → URI
            val mimeType = context.contentResolver.getType(mediaUri) ?: "image/*" // tipul fișierului

            whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType                                        // imagine sau video
                setPackage("com.whatsapp")                            // forțează WhatsApp
                putExtra(Intent.EXTRA_STREAM, mediaUri)               // fișierul atașat
                putExtra(Intent.EXTRA_TEXT, message)                  // mesajul text
                putExtra("jid", "$cleanPhone@s.whatsapp.net")         // contactul destinatar
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)       // permisiune citire fișier
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)               // necesar din BroadcastReceiver

                clipData = ClipData.newRawUri("", mediaUri).apply {
                    addItem(ClipData.Item(message))                   // necesar pe Android 10+ pentru permisiuni URI
                }
            }
        }

        //Acest bloc construiește și afișează notificarea pe telefon.
        val pendingIntent = PendingIntent.getActivity(
            context,
            name.hashCode(),
            whatsappIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "bday_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Obligatoriu pe Android 8.0+, altfel notificarea nu apare
            val channel = NotificationChannel(channelId, "Zile de Nastere", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle("Zi de naștere: $name!")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH) //// apare ca popup
            .setContentIntent(pendingIntent) // ce face la apăsare
            .setAutoCancel(true) // dispare după apăsare
            .build()

        notificationManager.notify(name.hashCode(), notification)
    }
}