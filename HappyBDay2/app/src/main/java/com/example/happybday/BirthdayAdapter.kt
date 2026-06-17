package com.example.happybday

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//Ia lista de obiecte Birthday din baza de date și le afișează vizual în RecyclerView
class BirthdayAdapter(
    private var list: List<Birthday>,
    private val onEditClick: (Birthday) -> Unit // Adăugăm un callback pentru editare
) : RecyclerView.Adapter<BirthdayAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
    }

    //layout-ul XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_birthday, parent, false)
        return ViewHolder(view)
    }

    //populează datele în item-ul de la poziția dată
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bday = list[position]
        holder.tvName.text = bday.name
        holder.tvDate.text = "Data: ${bday.day}/${bday.month} | Ora: ${bday.hour}:${String.format("%02d", bday.minute)}"

        if (bday.mediaUri.isNotEmpty()) {
            holder.ivThumbnail.visibility = View.VISIBLE
            try {
                val uri = Uri.parse(bday.mediaUri)
                val mimeType = holder.itemView.context.contentResolver.getType(uri)
                if (mimeType != null && mimeType.startsWith("video/")) {
                    holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_gallery)
                } else {
                    holder.ivThumbnail.setImageURI(null)
                    holder.ivThumbnail.setImageURI(uri)
                }
            } catch (e: Exception) {
                // Dacă nu avem permisiune sau URI-ul e invalid, punem un icon de eroare sau placeholder
                holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        } else {
            holder.ivThumbnail.visibility = View.GONE
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(bday)
        }
    }

    //returnează numărul de iteme
    override fun getItemCount() = list.size

    fun updateData(newList: List<Birthday>) {
        list = newList
        notifyDataSetChanged()
    }
}