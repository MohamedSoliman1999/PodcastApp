# PodcastApp 🎙️

<div align="center">
  <img src="https://raw.githubusercontent.com/your-username/your-repo/main/art/hero_banner.png" width="100%" alt="PodcastApp Banner" />

  <br />

  <div>
    <strong>Arabic-First · Jetpack Compose · Clean Architecture</strong>
  </div>

  <br />

  <p>
    A production-grade Arabic/English podcast client built with modern Android architecture, 
    real audio playback, and a flicker-free cold start.
  </p>

  <div>
    <img src="https://img.shields.io/badge/v1.0.0-gold?style=flat-square" alt="Version" />
    <img src="https://img.shields.io/badge/Kotlin-2.0.21-purple?style=flat-square" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Hilt-2.52-blue?style=flat-square" alt="Hilt" />
    <img src="https://img.shields.io/badge/Media3-1.4.1-green?style=flat-square" alt="Media3" />
  </div>
</div>

---

## 📱 Interface Preview

<table width="100%">
  <tr>
    <td width="33%"><img src="[https://raw.githubusercontent.com/your-username/your-repo/main/art/screen_home.png](https://github.com/user-attachments/assets/83d6828f-2f31-4f57-aa0e-53bc2e6619fa)" width="100%" /></td>
    <td width="33%"><img src="[https://raw.githubusercontent.com/your-username/your-repo/main/art/screen_search.png](https://github.com/user-attachments/assets/9a9ec523-3532-414b-9f4a-4a938f94664e)" width="100%" /></td>
    <td width="33%"><img src="[https://raw.githubusercontent.com/your-username/your-repo/main/art/screen_settings.png](https://github.com/user-attachments/assets/c8173318-3737-4953-936b-c3c6931dbe73)" width="100%" /></td>
  </tr>
  <tr align="center">
    <td><strong>Home (RTL)</strong></td>
    <td><strong>Search</strong></td>
    <td><strong>Settings</strong></td>
  </tr>
</table>

---

## 🏗️ Architecture & Tech Stack

This project follows **Clean Architecture** principles to ensure scalability, testability, and a clear separation of concerns.



### Core Stack
* **UI:** Jetpack Compose (1.7+) with Material 3.
* **DI:** Hilt (Dependency Injection) for modularity.
* **Async:** Kotlin Coroutines & Flow (StateFlow/SharedFlow).
* **Local Data:** Room DB & Preferences DataStore (for AppSettings).
* **Playback:** Media3 ExoPlayer with MediaSession integration.
* **Image Loading:** Coil (optimized for high-res podcast art).

---

## 🛠️ Key Features

* **⚡ Optimized Startup:** Implements `enableEdgeToEdge()` and pre-calculated theme states to prevent light/dark flickering.
* **🌍 Multi-Language:** Native support for Arabic (RTL) and English (LTR) with instant in-app switching.
* **🔍 Smart Search:** Debounced search input (200ms) to reduce API overhead and prevent race conditions.
* **📻 Background Playback:** Fully integrated Media3 service with notification controls.

---

## 🧪 Testing Coverage

The project maintains a robust test suite using **JUnit 5**, **MockK**, and **Turbine**.

| Layer | Tooling | Focus |
| :--- | :---

## 🧪 Vedio

https://github.com/user-attachments/assets/7512fbc6-b359-49e3-8d32-d41ef1ee7285
