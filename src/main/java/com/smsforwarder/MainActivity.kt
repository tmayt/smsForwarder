package com.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
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
    private lateinit var buttonToggleEnabled: MaterialButton
    private lateinit var editWebhookUrl: EditText
    private lateinit var editCustomHeaders: TextInputEditText
    private lateinit var buttonSave: Button
    private lateinit var buttonAddCondition: MaterialButton
    private lateinit var recyclerViewConditions: RecyclerView
    private lateinit var conditionAdapter: ConditionAdapter
    
    private val PERMISSION_REQUEST_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        settingsManager = SettingsManager(this)
        
        initViews()
        setupRecyclerView()
        loadSettings()
        setupClickListeners()
        
        // درخواست مجوزها
        requestPermissions()
    }
    
    private fun initViews() {
        buttonToggleEnabled = findViewById(R.id.buttonToggleEnabled)
        editWebhookUrl = findViewById(R.id.editWebhookUrl)
        editCustomHeaders = findViewById(R.id.editCustomHeaders)
        buttonSave = findViewById(R.id.buttonSave)
        buttonAddCondition = findViewById(R.id.buttonAddCondition)
        recyclerViewConditions = findViewById(R.id.recyclerViewConditions)
    }
    
    private fun setupRecyclerView() {
        conditionAdapter = ConditionAdapter(
            conditions = emptyList(),
            onEditClick = { condition -> showConditionDialog(condition) },
            onDeleteClick = { condition -> deleteCondition(condition) }
        )
        recyclerViewConditions.layoutManager = LinearLayoutManager(this)
        recyclerViewConditions.adapter = conditionAdapter
    }
    
    private fun loadSettings() {
        updateToggleButton()
        editWebhookUrl.setText(settingsManager.getWebhookUrl())
        editCustomHeaders.setText(settingsManager.getCustomHeaders())
        conditionAdapter.updateConditions(settingsManager.getConditions())
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
        
        val dialog = AlertDialog.Builder(this)
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
        
        dialog.show()
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
        
        if (webhookUrl.isBlank()) {
            Toast.makeText(this, "لطفاً آدرس وب‌هوک را وارد کنید", Toast.LENGTH_SHORT).show()
            return
        }
        
        // بررسی صحت JSON هدرها (اگر وارد شده باشد)
        if (customHeaders.isNotBlank()) {
            try {
                val jsonObject = org.json.JSONObject(customHeaders)
                // اگر JSON معتبر باشد، ادامه می‌دهیم
            } catch (e: Exception) {
                Toast.makeText(this, "فرمت JSON هدرها نامعتبر است. لطفاً بررسی کنید.", Toast.LENGTH_LONG).show()
                return
            }
        }
        
        val conditions = settingsManager.getConditions()
        if (conditions.isEmpty()) {
            Toast.makeText(this, "لطفاً حداقل یک شرط اضافه کنید", Toast.LENGTH_SHORT).show()
            return
        }
        
        // ذخیره تنظیمات
        settingsManager.setWebhookUrl(webhookUrl)
        settingsManager.setCustomHeaders(customHeaders)
        
        Toast.makeText(this, "تنظیمات ذخیره شد", Toast.LENGTH_SHORT).show()
    }
    
    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECEIVE_SMS)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_SMS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
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
