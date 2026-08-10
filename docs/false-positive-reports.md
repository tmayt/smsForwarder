# False Positive Report Templates

Submit **release-signed APK** or SHA-256 hash. Replace `X.X.X` with your version (e.g. `1.0.11`).

**App facts (use in every report):**

```
App name:     SMS Forwarder / فوروارد پیامک
Package:      ir.tmayt.smsforwarder
Developer:    tha <thaiostream@gmail.com>
Source code:  https://github.com/tmayt/smsForwarder (MIT license)
Privacy:      https://github.com/tmayt/smsForwarder/blob/main/PRIVACY.md
Permission:   RECEIVE_SMS only (not READ_SMS)
Behavior:     User-configured SMS filter → HTTP to user's webhook; no C2; no developer backend
```

---

## Avast / AVG (same vendor)

**Form:** https://www.avast.com/false-positive-file-form.php  
**Detection names:** `Android:Evo-gen [Trj]`

**Subject:** False positive — SMS Forwarder utility (ir.tmayt.smsforwarder)

**Message:**

```
Hello Avast Security Team,

Our application is incorrectly detected as Android:Evo-gen [Trj] / trojan.

Application details:
- Name: SMS Forwarder (فوروارد پیامک)
- Package: ir.tmayt.smsforwarder
- Version: X.X.X
- SHA-256: [PASTE APK SHA-256]

This is a legitimate open-source automation tool published at:
https://github.com/tmayt/smsForwarder

Purpose:
The user configures filter rules and a webhook URL. When an incoming SMS matches
a user-defined rule, the app sends a JSON HTTP request to that user-controlled
endpoint. The developer does not operate any command-and-control server and does
not collect SMS data.

Security measures:
- First-run consent dialog before SMS permission
- App disabled by default
- Only RECEIVE_SMS permission (READ_SMS removed)
- No obfuscation, no packers, no hidden functionality
- Full source code available on GitHub

We believe the detection is a heuristic false positive due to the SMS + network
pattern, similar to Tasker or IFTTT SMS integrations used with user consent.

Please whitelist this application and signer certificate.

Contact: thaiostream@gmail.com
Thank you.
```

---

## Kaspersky

**Portal:** https://opentip.kaspersky.com/ (upload APK)  
**Also:** https://www.kaspersky.com/enterprise-security/contact-investigation  
**Detection:** `HEUR:Trojan-Banker.AndroidOS.Agent.rj`

**Message:**

```
False positive investigation request

Product detected: HEUR:Trojan-Banker.AndroidOS.Agent.rj
App: SMS Forwarder
Package: ir.tmayt.smsforwarder
Version: X.X.X

This is NOT a banking trojan. It is an open-source SMS-to-webhook forwarder
where the end user explicitly configures:
1) filter conditions (sender / message text)
2) destination webhook URL (user's own server)

Repository: https://github.com/tmayt/smsForwarder
License: MIT

The app does not:
- Steal banking credentials
- Hide its UI or operate without user consent
- Contact developer-controlled servers
- Use READ_SMS or accessibility abuse

The app does:
- Show consent on first launch
- Require user to enable forwarding
- Send matched SMS only to user-configured HTTPS webhook

We request reclassification and removal from banker/spy heuristics.

Developer: tha, thaiostream@gmail.com
```

---

## Avira / WithSecure

**Detections:** `ANDROID/Evo.AG1591426.Gen`, `Malware.ANDROID/Evo.AG1591426.Gen`

**Avira:** https://www.avira.com/en/analysis/submit

```
False positive submission — ir.tmayt.smsforwarder

Detection: ANDROID/Evo.AG1591426.Gen
File: smsforwarder-X.X.X.apk (release signed)

Legitimate open-source utility for user-configured SMS forwarding to a
user-defined webhook. Source: https://github.com/tmayt/smsForwarder

Not malware — no evasion, no C2, consent dialog, disabled by default.
Please update signature to clean.

thaiostream@gmail.com
```

---

## BitDefender

**Detection:** `Android.Riskware.SmsSpy.aBHN`  
**Form:** https://www.bitdefender.com/consumer/support/answer/29358/

```
Subject: False Positive — Android.Riskware.SmsSpy.aBHN

App: SMS Forwarder (ir.tmayt.smsforwarder)
Version: X.X.X

We understand Riskware classification for SMS tools. Our app is a transparent
automation utility:
- Open source: https://github.com/tmayt/smsForwarder
- User sets webhook and filters in visible UI
- Consent required at first run
- No data sent to developer

Request: Reclassify as clean or PUA with documented user consent, not SmsSpy trojan.

Contact: thaiostream@gmail.com
```

---

## Ikarus

**Detection:** `Trojan-Spy.AndroidOS.SMSSpy`

**Contact:** https://www.ikarussecurity.com/en/support

```
False positive — Trojan-Spy.AndroidOS.SMSSpy

Package: ir.tmayt.smsforwarder
Open source SMS forwarder with explicit user consent and user-defined webhook.
Not spyware — no covert operation. Source available for review at GitHub:
https://github.com/tmayt/smsForwarder

Please reanalyze. thaiostream@gmail.com
```

---

## Cynet

**Detection:** Malicious (score: 99)

Email vendor via sample submission page or VirusTotal vendor contact.

```
False positive reanalysis request — ir.tmayt.smsforwarder

High score appears driven by SMS exfiltration heuristic. This is intentional
user-configured functionality (webhook automation), not covert exfiltration:
- Consent dialog
- Open source
- No C2 infrastructure

https://github.com/tmayt/smsForwarder
thaiostream@gmail.com
```

---

## VirusTotal (optional comment when commenting on your file)

```
Owner here. Open-source SMS-to-webhook utility.
Source: https://github.com/tmayt/smsForwarder
False positive reports submitted to vendors. Not trojan/spyware.
```

---

## How to get SHA-256 of your APK

```bash
sha256sum build/outputs/apk/release/smsforwarder-release.apk
```

Or:

```bash
apksigner verify --print-certs build/outputs/apk/release/smsforwarder-release.apk
keytool -list -printcert -jarfile build/outputs/apk/release/smsforwarder-release.apk
```

---

## Tips for faster approval

1. Submit **same signed binary** you distribute on GitHub Releases  
2. Link **public source repo** in every form  
3. Attach **screenshots** of consent + settings UI  
4. One report per vendor; wait 3–10 business days  
5. After whitelist, re-upload to VirusTotal to verify count drops  
6. Keep **versionCode** and signing key stable across reports
