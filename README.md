<p align="center">
  <img src="assets/logo.svg" alt="Labroxy Logo" width="180" height="170" />
</p>

<h1 align="center">Labroxy</h1>

<p align="center">
  <strong>A premium, modern, and lightning-fast GitLab client for Android.</strong>
</p>

<p align="center">
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?logo=kotlin&logoColor=white&style=for-the-badge" alt="Kotlin" /></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Compose-Material%203-4285F4.svg?logo=android&logoColor=white&style=for-the-badge" alt="Jetpack Compose" /></a>
  <a href="https://ktor.io"><img src="https://img.shields.io/badge/Ktor-3.0.3-F05138.svg?logo=ktor&logoColor=white&style=for-the-badge" alt="Ktor Client" /></a>
  <a href="https://developer.android.com/studio"><img src="https://img.shields.io/badge/Min%20SDK-26-3DDC84.svg?logo=android&logoColor=white&style=for-the-badge" alt="Min SDK" /></a>
</p>

---

## 🚀 Overview

**Labroxy** is a native Android GitLab client built from the ground up using **Jetpack Compose** and **Material 3**. It is engineered for developers who want a fluid, modern, and gorgeous interface to manage their GitLab projects, merge requests, issues, and work pipelines on the go.

Whether you are using standard **GitLab.com** or a custom **self-managed GitLab host**, Labroxy keeps your workspace synchronized securely and efficiently.

---

## ✨ Features

Labroxy is packed with rich features designed to elevate your mobile GitLab experience:

### 🎛️ Your Work Dashboard
- **All-in-One Home Screen:** Quick access to Projects, Groups, and Work Items.
- **Stay Organized:** Direct filters for Assigned Work, Merge Requests, To-Do List, and Notifications.

### 🔍 Explore & Search
- **Full Scale Pagination:** Lists all accessible projects through GitLab’s cursor pagination instead of limiting to recent membership.
- **Deep Search:** Instantly search through projects, groups, and work items straight from their respective sections.

### 📊 Project Insights
- **Rich Project Details:** Detailed dashboards for individual projects, showing:
  - 🟢 Open Issues & Merge Requests
  - ⏱️ Recent Commit Pipelines
  - 📌 Issue Boards
  - 🍴 Branch Count, Stars, and Forks
  - 🏷️ Metadata & labels

### 🛡️ Core Usability
- **Secure Storage:** Your GitLab host URL and Personal Access Tokens are stored locally and securely using **Jetpack DataStore (Preferences)**.
- **Smart Navigation:** Fully integrated Android back-press handling for returning seamlessly from details or sub-sections without accidentally exiting.

---

## 🛠️ Tech Stack

Labroxy is built using modern Android architecture components and best-practice libraries:

| Component | Library / Framework | Description |
| :--- | :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) | Modern, expressive, and type-safe language. |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) | Declarative UI toolkit for building beautiful native Android interfaces. |
| **Design System** | [Material 3](https://m3.material.io/) | Next-generation of Material Design with dynamic colors and modern styling. |
| **Navigation** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) | Type-safe navigation engine for Jetpack Compose apps. |
| **Networking** | [Ktor Client](https://ktor.io/) | Multiplatform asynchronous HTTP client for structured and high-performance network calls. |
| **Serialization** | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | Kotlin-first, compiler-plugin-based JSON serialization. |
| **Local Storage** | [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) | Modern, data-consistent replacement for SharedPreferences. |
| **Image Loading** | [Coil Compose](https://coil-kt.github.io/coil/) | Fast, lightweight Kotlin-first image loader for Compose. |

---

## 🏁 Getting Started

### 📋 Prerequisites

To run Labroxy, you need:
- **Android Studio** (Koala or newer recommended)
- **Android SDK** installed (Targeting API level 35)
- A **GitLab Personal Access Token** (PAT) with:
  - `read_api` (required for pulling repository information, issues, and pipelines)
  - `read_repository` (recommended for full project metadata and committing references)

### 📥 Installation & Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Pankaj-Meharchandani/Labroxy.git
   cd Labroxy
   ```

2. **Open in Android Studio:**
   - Launch Android Studio and choose **Open an existing project**.
   - Select the `Labroxy` root directory.
   - Let Gradle sync and download all dependencies automatically.

3. **Run the App:**
   - Connect your physical device (with USB Debugging enabled) or start an emulator.
   - Select the `app` configuration in the toolbar and click the **Run** button (or press `Shift + F10`).

---

## ⚙️ Configuration & Access

When you launch Labroxy for the first time, you will be prompted to enter your GitLab connection parameters:

1. **GitLab Instance Host:**
   - For public repositories, use standard `https://gitlab.com`.
   - For self-managed hosts, enter your server's base URL (e.g., `https://gitlab.yourcompany.com`).
2. **Personal Access Token:**
   - Paste your secure Personal Access Token.
   - *Note: Labroxy stores this token locally using Android's safe Jetpack DataStore and never transmits it to any third-party servers except your configured GitLab instance.*

---

## 🤝 Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you'd like to contribute:
1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

<p align="center">
  <sub>Built with ❤️ by Pankaj Meharchandani and contributors.</sub>
</p>
