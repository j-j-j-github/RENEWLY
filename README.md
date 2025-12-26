Renewly – Smart Subscription Manager 💳📱

⸻

Renewly is a premium Android application designed to help users track, manage, and never miss subscription renewals. Built with Jetpack Compose and Material 3, it blends modern Android design with fluid, Apple‑inspired aesthetics for a polished, high‑end experience.

⸻

📱 Features

⸻

	•	Smart Countdown
Real‑time tracking that displays days and hours remaining until the next billing cycle.
	•	Automated Renewals
Built‑in logic for monthly and yearly subscriptions with automatic date calculations.
	•	Dynamic Subscription Cards
Cards generate rich gradient backgrounds dynamically based on the brand’s primary color.
	•	Fluid Side Navigation
Custom right‑side drawer with a smooth 350ms ease‑in‑out animation.
	•	Backdrop Blur (Android 12+)
Real‑time Gaussian blur and dimming applied to background content when the sidebar is open.
	•	Graceful Fallback
Devices below Android 12 display a clean semi‑transparent overlay instead of blur.
	•	Brand Icon Support
Upload custom subscription icons via Firebase, with local fallback icons generated from brand initials.
	•	Custom Currency Selector
Scrollable dropdown with a persistent scrollbar supporting 15+ global currencies (USD, EUR, INR, GBP, JPY, and more).
	•	Profile Management
Upload and update profile pictures directly from the gallery with instant UI updates.
	•	Password Recovery
Reset passwords seamlessly from within the sidebar.
	•	Collapsible About Section
Interactive section showing app version, creator details, and a “Report a Problem” mailto link.

⸻

🛠️ Tech Stack

⸻

	•	Language: Kotlin
	•	UI: Jetpack Compose (Material 3)
	•	Architecture: MVVM (Model‑View‑ViewModel) with StateFlow
	•	Backend: Firebase Authentication, Firestore, Cloud Storage
	•	Image Loading: Coil (AsyncImage, circular clipping support)

⸻

📂 Project Overview

⸻

	•	Compose‑First UI — Fully declarative UI built with Jetpack Compose
	•	State‑Driven Design — Reactive UI powered by StateFlow
	•	Firebase Integration — Secure authentication, real‑time data, and image storage
	•	Modern Animations — Smooth transitions, gradients, and blur effects using modern Android rendering APIs

⸻

🛠️ Setup & Installation

⸻

Clone the Repository

git clone https://github.com/yourusername/renewly.git


⸻

Firebase Configuration
	•	Place google-services.json inside the /app directory
	•	Enable the following services in Firebase Console:
	•	Authentication (Email / Password)
	•	Cloud Firestore
	•	Cloud Storage

⸻

Requirements
	•	Latest Android Studio with Jetpack Compose support
	•	Android 12 (API 31+) for blur effects (optional but recommended)

⸻

👤 Author

⸻

Jeeval Jolly Jacob
Creator & Lead Developer

⸻

Renewly brings clarity and elegance to subscription management, helping users stay in control with a smooth, modern Android experience.
