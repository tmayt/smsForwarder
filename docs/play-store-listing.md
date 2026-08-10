# Play Store Listing Text

## Short description (80 chars max)

**English:**  
`Forward matching SMS to your webhook — filters, rules, full user control`

**فارسی:**  
`فوروارد پیامک به وب‌هوک شما — فیلتر شرطی، کنترل کامل`

---

## Full description — English

```
SMS Forwarder — Personal Webhook Automation

Forward incoming SMS messages to YOUR webhook — only when YOUR rules match.

YOU are in control:
• Define filter rules (sender number, exact match, message contains text)
• Set your own webhook URL (HTTPS recommended)
• Choose HTTP method: POST, GET, PUT, PATCH, DELETE
• Customize JSON payload template
• Enable or disable forwarding with one tap
• App is OFF by default until you turn it on

How it works:
1. Accept the consent screen (explains exactly what the app does)
2. Grant SMS receive permission
3. Add your webhook URL and conditions
4. Enable the app
5. Matching SMS are sent as JSON to your endpoint

Example payload:
{
  "text": "message body",
  "from": "sender number",
  "timestamp": 1234567890123
}

Privacy:
• No developer server — we never receive your messages
• No ads, no analytics SDK
• Settings stored locally on your device
• Open source: https://github.com/tmayt/smsForwarder

Use cases:
• Home automation (Home Assistant, Node-RED)
• Personal bots (Telegram, Discord webhooks)
• Workflow tools (n8n, Zapier, Make)
• Your own API for OTP or alert routing YOU configured

Legal: For personal use on devices you own or with explicit permission. You are responsible for your webhook and compliance with local laws.

Contact: thaiostream@gmail.com
Privacy policy: https://github.com/tmayt/smsForwarder/blob/main/PRIVACY.md
```

---

## Full description — فارسی

```
فوروارد پیامک — اتوماسیون وب‌هوک شخصی

پیامک‌های دریافتی را فقط وقتی به وب‌هوک شما می‌فرستد که شرط‌هایی که خودتان تعریف کرده‌اید برقرار باشد.

کنترل با شماست:
• تعریف شرط فیلتر (شماره فرستنده، تطابق دقیق، شامل بودن متن)
• وارد کردن آدرس وب‌هوک خودتان
• انتخاب متد HTTP و قالب JSON
• فعال/غیرفعال کردن با یک دکمه
• برنامه به‌صورت پیش‌فرض خاموش است

حریم خصوصی:
• بدون سرور توسعه‌دهنده — پیامک شما نزد ما نمی‌آید
• بدون تبلیغات و آنالیتیکس
• سورس باز: https://github.com/tmayt/smsForwarder

کاربرد: اتوماسیون شخصی، Home Assistant، ربات تلگرام، n8n و API خودتان.

مسئولیت استفاده قانونی و انتخاب وب‌هوک با کاربر است.

تماس: thaiostream@gmail.com
```

---

## Category

- **Primary:** Tools  
- **Tags:** automation, webhook, SMS, productivity

---

## Content rating questionnaire hints

- No violence, gambling, or user-generated public content
- App can transmit SMS content **only to user-configured endpoint** — disclose in questionnaire
- Not directed at children under 13

---

## What's new (example for next release)

**English:**  
`Removed unused READ_SMS permission. Added first-run consent dialog. Improved transparency for SMS forwarding.`

**فارسی:**  
`حذف مجوز غیرضروری READ_SMS. افزودن دیالوگ رضایت در اولین اجرا.`
