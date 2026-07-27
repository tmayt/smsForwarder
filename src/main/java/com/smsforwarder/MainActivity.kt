package com.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var logManager: LogManager
    private lateinit var buttonToggleEnabled: MaterialButton
    private lateinit var editWebhookUrl: EditText
    private lateinit var spinnerHttpMethod: Spinner
    private lateinit var editPayloadTemplate: TextInputEditText
    private lateinit var editCustomHeaders: TextInputEditText
    private lateinit var buttonSave: Button
    private lateinit var buttonAddCondition: MaterialButton
    private lateinit var recyclerViewConditions: RecyclerView
    private lateinit var recyclerViewLogs: RecyclerView
    private lateinit var textLogsEmpty: TextView
    private lateinit var buttonRefreshLogs: MaterialButton
    private lateinit var buttonClearLogs: MaterialButton
    private lateinit var conditionAdapter: ConditionAdapter
    private lateinit var logAdapter: LogAdapter

    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingsManager = SettingsManager(this)
        logManager = LogManager(this)

        initViews()
        setupHttpMethodSpinner()
        setupRecyclerViews()
        loadSettings()
        loadLogs()
        setupClickListeners()
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        loadLogs()
    }

    private fun initViews() {
        buttonToggleEnabled = findViewById(R.id.buttonToggleEnabled)
        editWebhookUrl = findViewById(R.id.editWebhookUrl)
        spinnerHttpMethod = findViewById(R.id.spinnerHttpMethod)
        editPayloadTemplate = findViewById(R.id.editPayloadTemplate)
        editCustomHeaders = findViewById(R.id.editCustomHeaders)
        buttonSave = findViewById(R.id.buttonSave)
        buttonAddCondition = findViewById(R.id.buttonAddCondition)
        recyclerViewConditions = findViewById(R.id.recyclerViewConditions)
        recyclerViewLogs = findViewById(R.id.recyclerViewLogs)
        textLogsEmpty = findViewById(R.id.textLogsEmpty)
        buttonRefreshLogs = findViewById(R.id.buttonRefreshLogs)
        buttonClearLogs = findViewById(R.id.buttonClearLogs)
    }

    private fun setupHttpMethodSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            SettingsManager.HTTP_METHODS
        )
        spinnerHttpMethod.adapter = adapter
    }

    private fun setupRecyclerViews() {
        conditionAdapter = ConditionAdapter(
            conditions = emptyList(),
            onEditClick = { condition -> showConditionDialog(condition) },
            onDeleteClick = { condition -> deleteCondition(condition) }
        )
        recyclerViewConditions.layoutManager = LinearLayoutManager(this)
        recyclerViewConditions.adapter = conditionAdapter

        logAdapter = LogAdapter()
        recyclerViewLogs.layoutManager = LinearLayoutManager(this)
        recyclerViewLogs.adapter = logAdapter
    }

    private fun loadSettings() {
        updateToggleButton()
        editWebhookUrl.setText(settingsManager.getWebhookUrl())
        editPayloadTemplate.setText(settingsManager.getPayloadTemplate())
        editCustomHeaders.setText(settingsManager.getCustomHeaders())
        conditionAdapter.updateConditions(settingsManager.getConditions())

        val methodIndex = SettingsManager.HTTP_METHODS.indexOf(settingsManager.getHttpMethod())
        if (methodIndex >= 0) {
            spinnerHttpMethod.setSelection(methodIndex)
        }
    }

    private fun loadLogs() {
        val logs = logManager.getLogs()
        logAdapter.updateLogs(logs)
        textLogsEmpty.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
        recyclerViewLogs.visibility = if (logs.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateToggleButton() {
        val isEnabled = settingsManager.isEnabled()
        if (isEnabled) {
            buttonToggleEnabled.text = "غیرفعال کردن برنامه"
            buttonToggleEnabled.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.error_red)
            )
            buttonToggleEnabled.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        } else {
            buttonToggleEnabled.text = "فعال کردن برنامه"
            buttonToggleEnabled.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.success_green)
            )
            buttonToggleEnabled.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        }
    }

    private fun setupClickListeners() {
        buttonToggleEnabled.setOnClickListener {
            toggleEnabled()
        }

        buttonSave.setOnClickListener {
            saveSettings()
        }

        buttonAddCondition.setOnClickListener {
            showConditionDialog(null)
        }

        buttonRefreshLogs.setOnClickListener {
            loadLogs()
            Toast.makeText(this, "لاگ‌ها بروزرسانی شد", Toast.LENGTH_SHORT).show()
        }

        buttonClearLogs.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("پاک کردن لاگ‌ها")
                .setMessage("آیا مطمئن هستید؟")
                .setPositiveButton("پاک کردن") { _, _ ->
                    logManager.clearLogs()
                    loadLogs()
                    Toast.makeText(this, "لاگ‌ها پاک شد", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("لغو", null)
                .show()
        }
    }

    private fun toggleEnabled() {
        val currentState = settingsManager.isEnabled()
        settingsManager.setEnabled(!currentState)
        updateToggleButton()

        val message = if (!currentState) {
            "برنامه فعال شد"
        } else {
            "برنامه غیرفعال شد"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showConditionDialog(condition: Condition?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_condition, null)

        val editName = dialogView.findViewById<TextInputEditText>(R.id.editConditionName)
        val editSender = dialogView.findViewById<TextInputEditText>(R.id.editSender)
        val editExactSender = dialogView.findViewById<TextInputEditText>(R.id.editExactSender)
        val editMessage = dialogView.findViewById<TextInputEditText>(R.id.editMessage)

        if (condition != null) {
            editName.setText(condition.name)
            editSender.setText(condition.sender)
            editExactSender.setText(condition.exactSender)
            editMessage.setText(condition.message)
        }

        AlertDialog.Builder(this)
            .setTitle(if (condition == null) "افزودن شرط جدید" else "ویرایش شرط")
            .setView(dialogView)
            .setPositiveButton("ذخیره") { _, _ ->
                val name = editName.text.toString().trim()
                val sender = editSender.text.toString().trim()
                val exactSender = editExactSender.text.toString().trim()
                val message = editMessage.text.toString().trim()

                if (sender.isBlank() && exactSender.isBlank() && message.isBlank()) {
                    Toast.makeText(this, "حداقل یکی از فیلدها باید پر شود", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newCondition = if (condition == null) {
                    Condition(name = name, sender = sender, exactSender = exactSender, message = message)
                } else {
                    condition.copy(name = name, sender = sender, exactSender = exactSender, message = message)
                }

                if (condition == null) {
                    settingsManager.addCondition(newCondition)
                } else {
                    settingsManager.updateCondition(newCondition)
                }

                loadSettings()
                Toast.makeText(this, "شرط ذخیره شد", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("لغو", null)
            .create()
            .show()
    }

    private fun deleteCondition(condition: Condition) {
        AlertDialog.Builder(this)
            .setTitle("حذف شرط")
            .setMessage("آیا مطمئن هستید که می‌خواهید این شرط را حذف کنید؟")
            .setPositiveButton("حذف") { _, _ ->
                settingsManager.deleteCondition(condition.id)
                loadSettings()
                Toast.makeText(this, "شرط حذف شد", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("لغو", null)
            .show()
    }

    private fun saveSettings() {
        val webhookUrl = editWebhookUrl.text.toString().trim()
        val customHeaders = editCustomHeaders.text.toString().trim()
        val payloadTemplate = editPayloadTemplate.text.toString().trim()
        val httpMethod = spinnerHttpMethod.selectedItem.toString()

        if (webhookUrl.isBlank()) {
            Toast.makeText(this, "لطفاً آدرس وب‌هوک را وارد کنید", Toast.LENGTH_SHORT).show()
            return
        }

        if (customHeaders.isNotBlank()) {
            try {
                org.json.JSONObject(customHeaders)
            } catch (e: Exception) {
                Toast.makeText(this, "فرمت JSON هدرها نامعتبر است", Toast.LENGTH_LONG).show()
                return
            }
        }

        if (payloadTemplate.isNotBlank()) {
            try {
                val sample = PayloadBuilder.build(
                    payloadTemplate,
                    "test",
                    "+989121234567",
                    System.currentTimeMillis()
                )
                org.json.JSONObject(sample)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "قالب Payload نامعتبر است. JSON معتبر با متغیرها وارد کنید.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        val conditions = settingsManager.getConditions()
        if (conditions.isEmpty()) {
            Toast.makeText(this, "لطفاً حداقل یک شرط اضافه کنید", Toast.LENGTH_SHORT).show()
            return
        }

        settingsManager.setWebhookUrl(webhookUrl)
        settingsManager.setCustomHeaders(customHeaders)
        settingsManager.setHttpMethod(httpMethod)
        settingsManager.setPayloadTemplate(
            payloadTemplate.ifBlank { PayloadBuilder.DEFAULT_TEMPLATE }
        )

        Toast.makeText(this, "تنظیمات ذخیره شد", Toast.LENGTH_SHORT).show()
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.RECEIVE_SMS)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_SMS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.RECEIVE_SMS)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!allGranted) {
                Toast.makeText(
                    this,
                    "برای عملکرد صحیح برنامه، مجوزهای SMS لازم است",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
