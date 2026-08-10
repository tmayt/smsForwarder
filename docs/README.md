# مستندات انتشار و گزارش False Positive

این پوشه متن‌های آماده برای:

1. **درخواست مجوز SMS در Google Play Console**
2. **متن لیستینگ Play Store**
3. **فرم‌های False Positive** (Avast، Kaspersky، سایر آنتی‌ویروس‌ها)
4. **درخواست تجدیدنظر Google Play Protect**

## اطلاعات ثابت برنامه

| فیلد | مقدار |
|------|--------|
| نام برنامه | فوروارد پیامک (SMS Forwarder) |
| Package ID | `ir.tmayt.smsforwarder` |
| توسعه‌دهنده | tha |
| ایمیل | thaiostream@gmail.com |
| مخزن سورس | https://github.com/tmayt/smsForwarder |
| سیاست حریم خصوصی | `PRIVACY.md` در ریشه مخزن (یا URL منتشرشده در GitHub Pages) |

## چک‌لیست قبل از ارسال

- [ ] APK **release** با همان keystore امضا شده باشد (نه debug)
- [ ] SHA-256 امضای APK را از `keytool` یا Play Console بردارید
- [ ] لینک سیاست حریم خصوصی عمومی (مثلاً `https://github.com/tmayt/smsForwarder/blob/main/PRIVACY.md`)
- [ ] اسکرین‌شات از دیالوگ رضایت اولیه و صفحه تنظیمات
- [ ] ویدیوی کوتاه (۱–۲ دقیقه) از جریان کار: نصب → رضایت → تنظیم وب‌هوک → تست پیامک

## فایل‌ها

| فایل | کاربرد |
|------|--------|
| [google-play-sms-declaration.md](google-play-sms-declaration.md) | پاسخ‌های فرم SMS Permission در Play Console |
| [play-store-listing.md](play-store-listing.md) | توضیحات فارسی/انگلیسی برای استور |
| [false-positive-reports.md](false-positive-reports.md) | متن آماده برای Avast، Kaspersky و بقیه |
| [play-protect-appeal.md](play-protect-appeal.md) | درخواست تجدیدنظر Play Protect |

## نکته مهم درباره Google Play

Google مجوز `RECEIVE_SMS` را فقط برای دسته‌های محدودی می‌دهد. این برنامه ابزار **اتوماسیون شخصی** است؛ ممکن است نیاز به توضیح دقیق و ویدیو دمو داشته باشد. اگر رد شد، گزینه‌های جایگزین (مثل Notification Listener برای برخی سناریوها) را در همان فایل Play ذکر کرده‌ایم.
