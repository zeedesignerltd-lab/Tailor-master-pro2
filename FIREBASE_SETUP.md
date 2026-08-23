# Firebase Setup — Tailor Master Pro (Login + Cloud Sync)

App ab Firebase use karti hai: **Authentication** (Google + Phone OTP) aur **Cloud Firestore**
(cloud database jo offline mein bhi kaam karti hai aur internet aane par khud sync ho jati hai).

Yeh steps sirf ek baar karne hain, apne Google account se (Firebase free tier — koi cost nahi).

## Step 1 — Firebase Project Banayein

1. Browser mein [console.firebase.google.com](https://console.firebase.google.com) kholein, apne Google account se login karein
2. "**Add project**" → naam dein (e.g. `TailorMasterPro`) → continue → Google Analytics off kar sakte hain → "**Create project**"

## Step 2 — Android App Add Karein

1. Project dashboard pe Android icon (</>) pe click karein
2. **Android package name** mein bilkul yeh likhein: `com.tailormaster.pro`
3. App nickname: `Tailor Master Pro` (optional)
4. **SHA-1 certificate** wale box mein yeh paste karein (Google Sign-In ke liye zaroori hai):
   ```
   FD:6D:D6:84:AA:0F:E6:D7:2C:E4:C2:3D:A7:BF:2E:60:61:4E:59:8B
   ```
5. "**Register app**" → "**Download google-services.json**" — yeh file download kar lein
6. "Next" → "Next" → "Continue to console"

## Step 3 — google-services.json Repo Mein Daalein

Downloaded `google-services.json` file ko apne GitHub repo mein **`app/`** folder ke andar (root ke `app` folder mein, jahan `build.gradle.kts` hai) upload kar dein — same tareeqa jo pehle files upload karne ke liye use kiya tha.

**Zaroori**: file ka naam exact `google-services.json` hi rehna chahiye, aur `app` folder ke andar seedha (kisi sub-folder mein nahi).

## Step 4 — Authentication Enable Karein

1. Firebase console ke left menu mein "**Build → Authentication**" pe jayein
2. "**Get started**" pe click karein
3. **Sign-in method** tab mein:
   - "**Google**" pe click karein → Enable karein → project support email select karein → Save
   - "**Phone**" pe click karein → Enable karein → Save

## Step 5 — Firestore Database Banayein

1. Left menu mein "**Build → Firestore Database**" pe jayein
2. "**Create database**" → location select karein (koi bhi qareeb wali, e.g. `asia-south1`) → "**Start in production mode**" select karein → Enable

3. Ab **Rules** tab pe jayein aur default rules ko is se replace kar dein (yeh ensure karta hai ke har user sirf apna data dekh sake):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

4. "**Publish**" pe click karein

## Step 6 — Phone Number Testing (Optional lekin Recommended)

Real SMS OTP bhejne ke liye Firebase ko thoda waqt lagta hai verify karne mein (reCAPTCHA/Play Integrity), pehli baar test karte waqt kabhi dikkat ho sakti hai. Testing ke liye ek fake number add kar sakte hain:

1. Authentication → Sign-in method → "Phone" → neeche "**Phone numbers for testing**" section mein
2. Apna number aur ek fixed 6-digit code add kar dein (e.g. `+923001234567` → `123456`) — is number se login karte waqt yeh fixed code hamesha kaam karega, real SMS nahi aayega

## Bas Itna Hi

Yeh sab hone ke baad GitHub Actions se dobara APK build karein — login aur cloud sync kaam karne lagega.

**Note**: yeh app ab ek fixed debug-signing key (`keystore/debug.keystore`) use karti hai jo repo mein shamil hai, taake har CI build ka SHA-1 fingerprint same rahe aur Google Sign-In tootay na. Isay delete na karein.
