package com.example.ccm

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class GroupRecyclerViewAdapter(val items : MutableList<GroupCard>) : RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.group_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item : GroupCard) {
            val cardTitle = itemView.findViewById<TextView>(R.id.group_card_title)
            val cardDescription = itemView.findViewById<TextView>(R.id.group_card_description)
            val cardCurrentWeek = itemView.findViewById<TextView>(R.id.group_card_current_week)
            val cardEndWeek = itemView.findViewById<TextView>(R.id.group_card_end_week)
            val cardProgress = itemView.findViewById<ProgressBar>(R.id.group_card_progressbar)

            cardTitle.text = item.groupTitle
            cardDescription.text = item.groupDescription
        }
    }
}