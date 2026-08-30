# TerraAble 🌿♿

> **Travel that works for people and the planet.**

TerraAble is an AI-powered accessible and sustainable travel planner built with **Kotlin** and **Jetpack Compose (Material 3)**. It moves beyond standard navigation apps by solving multi-objective trade-offs across accessibility, carbon emissions, budget, transfers, walking distance, and real-time weather constraints.

---

## 🚀 Key Features

- 🧠 **Natural Language Intent Parsing**: Converts freeform trip requests (e.g. *"Plan a 4-day trip from Mumbai to Goa for my parents with a wheelchair..."*) into structured constraints.
- ⚖ **Pareto-Optimal Travel Plans**: Generates 4 mathematically optimized plans:
  - **🌱 Greenest**: Lowest CO₂e impact.
  - **♿ Most Accessible**: Maximum verified accessibility confidence & lowest continuous walking.
  - **⚖ Best Balance (Recommended)**: Optimal trade-off between mobility constraints, comfort, carbon, and budget.
  - **⚡ Fastest**: Minimum duration benchmark.
- 💡 **Explainable Recommendations ("Why This?")**: Explicitly breaks down trade-offs, answering critical questions like *"Why isn't the greenest option the best?"* (e.g., when the greenest route violates wheelchair walking thresholds).
- 📋 **Accessibility Passport & Evidence Layer**: Categorizes accessibility information into 4 evidence states:
  - `✓ Verified` (Official operator audit)
  - `◉ Reported` (Community reviews)
  - `◐ Inferred` (AI station profiling)
  - `? Unknown` (Explicit missing data warning)
- 🌦 **Open-Meteo Weather Integration**: Real-time hourly weather forecasts, rain probability charts, and weather impact advisories for transit connections.
- 🧪 **"What If?" Interactive Simulator**: Explore instant constraint modifications (strict 200m walking, greener alternatives, budget boosts) with live before/after diffs.
- 🚨 **Emergency SOS Screen**: 3-second hold-to-confirm emergency trigger, category selection, and GPS coordinate broadcast.

---

## 🛠 Tech Stack

- **Platform**: Android (Min SDK 26, Target SDK 34)
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose & Material 3
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Navigation**: Jetpack Navigation Compose
- **APIs**:
  - **TomTom Search & Routing API**
  - **Open-Meteo Forecast API**
- **Design**: OLED Dark Theme, large rounded cards (24–32dp), custom charts, and glassmorphic elevated surfaces.

---

## 🏃 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/SatyamPandey-07/TerraAble.git
   ```
2. Open the project in **Android Studio Hedgehog / Iguana / Jellyfish**.
3. (Optional) Add your TomTom API key in `local.properties`:
   ```properties
   TOMTOM_API_KEY=your_tomtom_api_key_here
   ```
4. Build and run on an Android device or emulator!
