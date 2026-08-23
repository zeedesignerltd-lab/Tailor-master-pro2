# Tailor Master Pro

Offline Android app (Kotlin + Jetpack Compose + Room) for single-tailor business management.

## Mobile se APK kaise banayein (GitHub Actions)

1. GitHub app ya browser mein login karein.
2. Naya **repository** banayein (e.g. `tailor-master-pro`), Public ya Private — koi farq nahi.
3. Is poore folder ka content us repo mein upload karein:
   - GitHub app/website pe "Add file" → "Upload files" se seedha ZIP nikal kar sab files/folders upload kar sakte hain (drag & drop ya select).
   - Zaroori: `.github/workflows/build-apk.yml` file bhi zaroor upload honi chahiye — yehi cloud build ko trigger karti hai.
4. Upload ke baad "Commit changes" dabayein (branch: `main`).
5. Repo ke **Actions** tab mein jayein — "Build APK" workflow khud chal jayega (2-4 minute lagte hain).
6. Jab workflow ka status green tick ho jaye, us run ko open karein → neeche **Artifacts** section mein "TailorMasterPro-debug-apk" milega — wahan se APK download kar lein.
7. Downloaded ZIP ko extract karein, andar `app-debug.apk` file hogi — usay phone pe install kar lein (Settings > "Install unknown apps" allow karna padega).

Agar workflow run nahi hota khud, to Actions tab mein "Build APK" workflow select karke "Run workflow" button se manually bhi chala sakte hain.
