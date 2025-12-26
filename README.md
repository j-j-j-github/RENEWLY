# 📱 Renewly

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)](https://developer.android.com/android)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack_Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-ffca28.svg)](https://firebase.google.com/)

**Renewly** is a high-performance subscription tracking application for Android. It is designed to give users total control over their recurring expenses through a premium, gesture-driven interface inspired by modern Apple-like aesthetics and Material 3 design principles.

---

## ✨ Features

### 💎 Premium Experience
* **Backdrop Blur Effect:** Utilizes `RenderEffect` (API 31+) to create a "glassmorphism" blur on the main dashboard whenever the sidebar is active.
* **Fluid Animations:** Right-to-left sliding sidebar with custom spring physics and synchronized background fading.
* **Dynamic Theming:** Supports full Light and Dark mode transitions with gradient-mapped subscription cards that adapt to brand colors.

### 📊 Subscription Management
* **Precision Tracking:** View remaining time in a "Days & Hours" countdown format for every service.
* **Auto-Calculated Cycles:** Handles monthly and yearly billing cycles automatically, refreshing the due date once a cycle passes.
* **Visual Branding:** Custom icon support via Firebase Storage with initials-based fallback icons for a clean look.

### 👤 Profile & Customization
* **Secure Auth:** Full login, signup, and password reset flow powered by Firebase Authentication.
* **Cloud Sync:** Profile data and subscription lists are synced across devices via Firestore.
* **Global Currencies:** A custom-built, scrollable selector supporting major world currencies with a green-gradient UI and custom scrollbar.

---

## 🛠 Tech Stack

| Layer | Technology |
| :--- | :--- |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3) |
| **Backend** | Firebase (Auth, Firestore, Storage) |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) |
| **Asynchronous** | Kotlin Coroutines & StateFlow |
| **Architecture** | MVVM (Model-View-ViewModel) |

---

## ⚙️ Setup Instructions

### Prerequisites
- Android Studio Ladybug (or newer).
- A Firebase Project set up in the Google Console.

### Configuration
1.  **Firebase:**
    * Download your `google-services.json` and place it in the `app/` folder.
    * Enable **Email/Password** in the Firebase Auth tab.
2.  **Assets:**
    * Ensure your app branding is in `res/drawable/renewly.png` to support the framed "About" section.
3.  **Build:**
    ```bash
    ./gradlew assembleDebug
    ```

---

## 📂 Project Structure

```text
com.example.renewly
├── data          # Subscription data models & CycleType enums
├── ui
│   ├── auth      # Auth screens, EditNameDialog, & ProfileViewModel
│   ├── subs      # SubscriptionListScreen & Sidebar UI components
│   └── theme     # Design system, Colors, & AppGradients
└── MainActivity  # Entry point and Navigation host
```

## ✍️ Author
Jeeval Jolly Jacob

