# Google Play — SMS Permission Declaration

Use this text when filling the **Permissions declaration form** in Play Console  
(Policy → App content → Sensitive app permissions → SMS).

---

## Core permission requested

- `android.permission.RECEIVE_SMS`

**Not requested:** `READ_SMS`, `SEND_SMS`, `READ_CALL_LOG`, `READ_CONTACTS`, location, or device admin.

---

## 1. Why does your app need SMS permission?

**English (copy-paste):**

> SMS Forwarder is a user-configured automation utility. The app listens for incoming SMS broadcasts on the device, evaluates them against filter rules defined entirely by the user (sender number and/or message text), and only when a rule matches, forwards the message content via HTTP POST/GET to a webhook URL that the user enters in the app settings.
>
> The developer does not operate any backend server and does not receive user SMS data. All filtering logic and configuration remain on the device. The user must explicitly accept a consent dialog on first launch before any SMS permission is requested.
>
> Primary use cases: personal automation (e.g. forward OTP or bank alerts to the user's own server, Telegram bot, Home Assistant, n8n, or Zapier webhook), debugging integrations, and notification routing for power users who control their own infrastructure.

**فارسی (برای مرجع داخلی):**

> برنامه ابزار اتوماسیون شخصی است. کاربر شرط فیلتر و آدرس وب‌هوک را خودش تنظیم می‌کند. فقط پیام‌های منطبق با شرط به URL کاربر ارسال می‌شوند. توسعه‌دهنده سرور مرکزی ندارد و داده‌ای دریافت نمی‌کند.

---

## 2. Declared use case category (Play Console dropdown)

Suggested selection (choose the closest match and justify in the text field):

**Option A — Cross-device sync / automation companion**  
If available: explain the user forwards SMS to their own cloud endpoint for use on another system.

**Option B — SMS-based financial transaction related**  
Only if your store listing and in-app examples focus on forwarding **user-initiated financial/OTP alerts** the user explicitly filters (not bulk collection). Be honest: the app is general-purpose filtering, not a banking app.

**Option C — Default handler / backup**  
**Do not claim** unless you implement default SMS app or backup features. This app does neither.

**Recommended honest framing:** Personal automation tool; user provides webhook; no developer data collection. Attach demo video.

---

## 3. How is SMS data used, stored, and shared?

**English:**

| Stage | Behavior |
|-------|----------|
| Collection | Incoming SMS is read from the `SMS_RECEIVED` broadcast intent only when the app is enabled by the user |
| On-device processing | Matched against user-defined conditions stored in local SharedPreferences |
| Transmission | Only matched messages are sent in one HTTP request to the user-configured webhook URL |
| Developer servers | None — no analytics, no ads, no crash reporting SDK that exfiltrates SMS |
| Retention | Optional local forward logs in app UI; cleared by user; removed on uninstall |
| Third parties | Only the user's chosen webhook endpoint (user responsibility) |

---

## 4. User control and transparency

**English:**

- First-run **consent dialog** explains SMS forwarding to user-configured webhook; user must accept or exit
- App is **disabled by default** until user taps enable
- User defines all filter conditions and webhook URL
- User can disable forwarding instantly without uninstalling
- Privacy policy: https://github.com/tmayt/smsForwarder/blob/main/PRIVACY.md
- Open source: https://github.com/tmayt/smsForwarder

**Screenshots to upload with declaration:**

1. Consent dialog (Persian text)
2. Main settings screen showing webhook URL + conditions
3. Disabled state (power button off)
4. Local logs screen (no cloud sync)

---

## 5. Video demo script (1–2 minutes)

Record in English or Persian with subtitles:

1. Install app → consent dialog appears → tap Accept
2. Grant RECEIVE_SMS when prompted
3. Show app is **disabled** by default
4. Enter a test webhook (e.g. webhook.site) and add condition (sender contains test number)
5. Enable app
6. Send test SMS from another phone
7. Show webhook received JSON `{ "text", "from", "timestamp" }`
8. Disable app → send another SMS → show it is **not** forwarded

Upload to YouTube (unlisted) and paste link in Play Console.

---

## 6. Data safety form (Play Console)

| Question | Answer |
|----------|--------|
| Does your app collect or share user data? | **Yes** — but only data the user chooses to forward via their webhook; not collected by developer |
| Is data encrypted in transit? | Depends on user's webhook (HTTPS recommended in app description) |
| Can users request data deletion? | Uninstall removes all local data; forwarded data is on user's server |
| Data types | SMS content, phone number of sender (only when user rules match) |
| Purpose | App functionality — user-configured automation |
| Optional / required | Required for core feature when user enables forwarding |

---

## 7. If Google rejects SMS permission

Alternatives to document in appeal or future version:

1. **Notification Listener API** — forward notifications instead of SMS (different permission, different UX; does not help for silent SMS)
2. **Manual export** — user copies SMS (defeats automation purpose)
3. **Distribution outside Play** — GitHub Releases with signed APK + false positive reports (current model)

Do **not** misrepresent the app category to obtain SMS permission.

---

## 8. Play Protect (sideloaded APK)

If users install APK from GitHub and Play Protect blocks it, see [play-protect-appeal.md](play-protect-appeal.md).

Publishing on Play Store with approved SMS declaration is the long-term fix for Play Protect reputation.
