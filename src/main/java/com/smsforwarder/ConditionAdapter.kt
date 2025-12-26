package com.smsforwarder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConditionAdapter(
    private var conditions: List<Condition>,
    private val onEditClick: (Condition) -> Unit,
    private val onDeleteClick: (Condition) -> Unit
) : RecyclerView.Adapter<ConditionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textConditionName)
        val textDetails: TextView = itemView.findViewById(R.id.textConditionDetails)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_condition, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val condition = conditions[position]
        
        holder.textName.text = condition.name.ifBlank { "شرط ${position + 1}" }
        
        val details = buildString {
            val parts = mutableListOf<String>()
            if (condition.sender.isNotBlank()) {
                parts.add("فرستنده شامل: ${condition.sender}")
            }
            if (condition.exactSender.isNotBlank()) {
                parts.add("فرستنده دقیق: ${condition.exactSender}")
            }
            if (condition.message.isNotBlank()) {
                parts.add("پیام شامل: ${condition.message}")
            }
            if (parts.isEmpty()) {
                append("شرط خالی است")
            } else {
                append(parts.joinToString(" | "))
            }
        }
        holder.textDetails.text = details
        
        holder.buttonEdit.setOnClickListener {
            onEditClick(condition)
        }
        
        holder.buttonDelete.setOnClickListener {
            onDeleteClick(condition)
        }
    }

    override fun getItemCount(): Int = conditions.size

    fun updateConditions(newConditions: List<Condition>) {
        conditions = newConditions
        notifyDataSetChanged()
    }
}

