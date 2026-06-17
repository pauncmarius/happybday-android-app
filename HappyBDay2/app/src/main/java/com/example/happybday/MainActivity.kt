package com.example.happybday

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper // acces la baza de date
    private lateinit var adapter: BirthdayAdapter // adaptorul pentru listă
    private lateinit var rvBirthdays: RecyclerView // lista vizuală
    private lateinit var etMonthFilter: EditText // filtrul de lună

    // Variabilă temporară pentru a ține minte poză/video
    private var currentSelectedMediaUri: String = ""
    private var tvMediaStatus: TextView? = null
    private var ivMediaPreview: ImageView? = null
    private var btnClearMedia: ImageButton? = null

    // Sistemul nou din Android pentru a prelua un singur fișier (Poză sau Video)
    private val pickMedia = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            // Acordăm permisiuni permanente pentru ca aplicația să poată citi fișierul
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                // Nu se poate lua permisiunea persistenta din toate sursele
            }
            currentSelectedMediaUri = uri.toString()
            updateMediaUI()
        }
    }
    //previzualizarea media
    private fun updateMediaUI() {
        val preview = ivMediaPreview ?: return
        val status = tvMediaStatus ?: return
        val clearBtn = btnClearMedia ?: return

        if (currentSelectedMediaUri.isNotEmpty()) {
            try {
                val uri = Uri.parse(currentSelectedMediaUri)
                status.text = "Media atașat!"
                preview.visibility = View.VISIBLE
                clearBtn.visibility = View.VISIBLE
                
                val mimeType = contentResolver.getType(uri)
                if (mimeType != null && mimeType.startsWith("video/")) {
                    preview.setImageResource(android.R.drawable.ic_menu_gallery)
                } else {
                    preview.setImageURI(null)
                    preview.setImageURI(uri)
                }
            } catch (e: Exception) {
                preview.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        } else {
            status.text = "Niciun media selectat"
            preview.visibility = View.GONE
            clearBtn.visibility = View.GONE
            preview.setImageURI(null)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedMediaUri", currentSelectedMediaUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentSelectedMediaUri = savedInstanceState.getString("selectedMediaUri", "")
    }

    //inițializarea ecranului principal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        dbHelper = DBHelper(this)
        rvBirthdays = findViewById(R.id.rvBirthdays)
        rvBirthdays.layoutManager = LinearLayoutManager(this)

        //// configurează RecyclerView + Adapter
        adapter = BirthdayAdapter(emptyList<Birthday>()) { birthdayToEdit: Birthday ->
            showAddBirthdayDialog(birthdayToEdit)
        }
        rvBirthdays.adapter = adapter

        etMonthFilter = findViewById(R.id.etMonthFilter)
        val btnFilter = findViewById<Button>(R.id.btnFilter)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        btnFilter.setOnClickListener { refreshList() }
        btnAdd.setOnClickListener { showAddBirthdayDialog() }
    }

    // boxul de adăugare/editare
    private fun showAddBirthdayDialog(existingBirthday: Birthday? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_birthday, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(if (existingBirthday == null) "Adaugă Sărbătorit" else "Editează Sărbătorit")

        val alertDialog = builder.show()

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
        val etDay = dialogView.findViewById<EditText>(R.id.etDay)
        val etMonth = dialogView.findViewById<EditText>(R.id.etMonth)
        val etHour = dialogView.findViewById<EditText>(R.id.etHour)
        val etMinute = dialogView.findViewById<EditText>(R.id.etMinute)
        val etMessage = dialogView.findViewById<EditText>(R.id.etMessage)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveBirthday)
        val btnPickMedia = dialogView.findViewById<Button>(R.id.btnPickMedia)
        tvMediaStatus = dialogView.findViewById(R.id.tvMediaStatus)
        ivMediaPreview = dialogView.findViewById(R.id.ivMediaPreview)
        btnClearMedia = dialogView.findViewById(R.id.btnClearMedia)

        // Resetăm variabila la deschiderea formularului
        currentSelectedMediaUri = existingBirthday?.mediaUri ?: ""
        updateMediaUI()

        //    // pre-completează câmpurile dacă e editare
        existingBirthday?.let {
            etName.setText(it.name)
            etPhone.setText(it.phone)
            etDay.setText(it.day.toString())
            etMonth.setText(it.month.toString())
            etHour.setText(it.hour.toString())
            etMinute.setText(it.minute.toString())
            etMessage.setText(it.message)
            btnSave.text = "Actualizează"
        }

        // Când apeși "Alege Media", se deschide galeria pentru orice tip de media
        btnPickMedia.setOnClickListener {
            pickMedia.launch(arrayOf("*/*"))
        }

        btnClearMedia?.setOnClickListener {
            currentSelectedMediaUri = ""
            updateMediaUI()
        }

        btnSave.setOnClickListener {
            try {
                val name = etName.text.toString()
                val phone = etPhone.text.toString()
                val day = etDay.text.toString().toInt()
                val month = etMonth.text.toString().toInt()
                val hour = etHour.text.toString().toInt()
                val minute = etMinute.text.toString().toInt()
                val message = etMessage.text.toString()

                if (name.isNotEmpty() && phone.isNotEmpty() && message.isNotEmpty()) {
                    if (existingBirthday == null) {
                        val newBirthday = Birthday(0, name, phone, month, day, hour, minute, message, currentSelectedMediaUri)
                        val id = dbHelper.addBirthday(newBirthday)
                        scheduleAlarm(newBirthday.copy(id = id.toInt()))
                    } else {
                        val updatedBirthday = Birthday(existingBirthday.id, name, phone, month, day, hour, minute, message, currentSelectedMediaUri)
                        dbHelper.updateBirthday(updatedBirthday)
                        scheduleAlarm(updatedBirthday)
                    }

                    refreshList()
                    alertDialog.dismiss()
                    Toast.makeText(this, "Salvat cu succes!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Completează toate câmpurile corect!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Eroare la introducerea datelor. Verifică numerele.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshList() {
        val monthStr = etMonthFilter.text.toString()
        if (monthStr.isNotEmpty()) {
            val list = dbHelper.getBirthdaysByMonth(monthStr.toInt())
            adapter.updateData(list)
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissions.isNotEmpty() && !permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1)
        }
    }

    private fun scheduleAlarm(bday: Birthday) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("name", bday.name)
            putExtra("phone", bday.phone)
            putExtra("message", bday.message)
            putExtra("mediaUri", bday.mediaUri)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, bday.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, bday.month - 1)
            set(Calendar.DAY_OF_MONTH, bday.day)
            set(Calendar.HOUR_OF_DAY, bday.hour)
            set(Calendar.MINUTE, bday.minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}