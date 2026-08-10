# Google Play Protect Appeal

For APK installed **outside Play Store** (e.g. GitHub Releases) when Play Protect shows **"App blocked"** or **"Harmful app"**.

---

## Where to appeal

1. **On device:** When Play Protect warning appears → Details → **Send for scanning** / **Install anyway** (wording varies by Android version)  
2. **Developer form:** https://support.google.com/googleplay/android-developer/contact/protectappeals  
3. **Safe Browsing (if Chrome blocks download):** https://safebrowsing.google.com/safebrowsing/report_error/

---

## Appeal form — English

**App package name:**  
`ir.tmayt.smsforwarder`

**App name:**  
`SMS Forwarder (فوروارد پیامک)`

**Developer email:**  
`thaiostream@gmail.com`

**Website / source:**  
`https://github.com/tmayt/smsForwarder`

**Why do you believe this is a false positive?**

```
SMS Forwarder is a legitimate open-source utility (MIT license) that lets the
device owner forward matching SMS messages to a webhook URL they configure.

Play Protect may flag it because SMS + network access matches spyware patterns.
However:

1. Full source code is public: https://github.com/tmayt/smsForwarder
2. No developer backend or C2 server
3. First-run consent dialog; app disabled by default
4. Only RECEIVE_SMS — not READ_SMS, SEND_SMS, or accessibility abuse
5. User explicitly sets webhook URL and filter rules in the UI
6. Privacy policy: https://github.com/tmayt/smsForwarder/blob/main/PRIVACY.md

Comparable legitimate tools: automation apps that forward notifications/SMS
with user consent for personal integrations (Home Assistant, n8n, etc.).

We request Play Protect reclassification so users who intentionally install
from our official GitHub releases are not blocked.

Signed release APK SHA-256: [PASTE]
Version: X.X.X
```

---

## For end users (share this in README / Releases)

**فارسی:**

اگر Play Protect مانع نصب شد:

1. مطمئن شوید APK را فقط از [صفحه Releases رسمی GitHub](https://github.com/tmayt/smsForwarder/releases) دانلود کرده‌اید  
2. در هشدار Play Protect گزینه «جزئیات» → «نصب با وجود هشدار» (در صورت وجود)  
3. اگر مسدود ماند، از توسعه‌دهنده بخواهید نسخه جدید را در Play Protect appeal کند  
4. گزارش false positive: https://support.google.com/googleplay/android-developer/contact/protectappeals

**English:**

If Play Protect blocks install, download only from official GitHub Releases, check warning details, and report false positive via Google's protect appeals form if needed.

---

## Long-term solution

Publishing on **Google Play** with approved SMS permission declaration (see [google-play-sms-declaration.md](google-play-sms-declaration.md)) builds trust and reduces Play Protect blocks for the same signing certificate.
