package com.smsforwarder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter(
    private var logs: List<LogEntry> = emptyList()
) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textLogType: TextView = itemView.findViewById(R.id.textLogType)
        val textLogTime: TextView = itemView.findViewById(R.id.textLogTime)
        val textLogMessage: TextView = itemView.findViewById(R.id.textLogMessage)
        val textLogDetails: TextView = itemView.findViewById(R.id.textLogDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        val context = holder.itemView.context

        holder.textLogType.text = typeLabel(log.type)
        holder.textLogType.setTextColor(ContextCompat.getColor(context, typeColor(log.type)))
        holder.textLogTime.text = timeFormat.format(Date(log.timestamp))
        holder.textLogMessage.text = log.message

        val details = buildDetails(log)
        if (details.isNotBlank()) {
            holder.textLogDetails.visibility = View.VISIBLE
            holder.textLogDetails.text = details
        } else {
            holder.textLogDetails.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = logs.size

    fun updateLogs(newLogs: List<LogEntry>) {
        logs = newLogs
        notifyDataSetChanged()
    }

    private fun typeLabel(type: LogEntry.LogType): String = when (type) {
        LogEntry.LogType.CONDITION_MATCHED -> "تشخیص شرط"
        LogEntry.LogType.FORWARDING -> "در حال فوروارد"
        LogEntry.LogType.HTTP_REQUEST_SENT,
        LogEntry.LogType.HTTP_POST_SENT -> "ارسال موفق"
        LogEntry.LogType.HTTP_REQUEST_FAILED,
        LogEntry.LogType.HTTP_POST_FAILED -> "ارسال ناموفق"
        LogEntry.LogType.ERROR -> "خطا"
        LogEntry.LogType.SMS_RECEIVED -> "دریافت SMS"
    }

    private fun typeColor(type: LogEntry.LogType): Int = when (type) {
        LogEntry.LogType.CONDITION_MATCHED -> R.color.primary_blue
        LogEntry.LogType.FORWARDING -> R.color.primary_blue_light
        LogEntry.LogType.HTTP_REQUEST_SENT,
        LogEntry.LogType.HTTP_POST_SENT -> R.color.success_green
        LogEntry.LogType.HTTP_REQUEST_FAILED,
        LogEntry.LogType.HTTP_POST_FAILED,
        LogEntry.LogType.ERROR -> R.color.error_red
        LogEntry.LogType.SMS_RECEIVED -> R.color.text_secondary_light
    }

    private fun buildDetails(log: LogEntry): String = buildString {
        if (log.sender.isNotBlank()) appendLine("از: ${log.sender}")
        if (log.conditionName.isNotBlank()) appendLine("شرط: ${log.conditionName}")
        if (log.httpMethod.isNotBlank()) appendLine("متد: ${log.httpMethod}")
        if (log.webhookUrl.isNotBlank()) appendLine("URL: ${log.webhookUrl}")
        if (log.payload.isNotBlank()) appendLine("Payload: ${log.payload}")
        if (log.responseCode > 0) appendLine("کد پاسخ: ${log.responseCode}")
        if (log.smsBody.isNotBlank()) append("پیام: ${log.smsBody.take(120)}")
    }.trim()
}
