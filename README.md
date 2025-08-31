# Navgurukul Task - Offline First Android App

This is an Android application built using **Kotlin** and **Jetpack Compose**.  
The project follows an **offline-first architecture** with background sync using **WorkManager** and **Room Database** for local persistence.  

---

## 🏛️ Architecture Overview

The project uses **MVVM (Model-View-ViewModel)** with the following components:

- **UI Layer** → Built with **Jetpack Compose**, observes state exposed by ViewModels.
- **ViewModel Layer** → Holds UI state and interacts with the repository.
- **Repository Layer** → Manages data from both local (Room DB) and remote (API) sources.
- **Local Database** → Implemented with **Room** for offline storage of students.
- **Remote Data Source** → Use firebase database to sync local data to server.
- **Dependency Injection** → Managed with **Hilt** for clean and testable architecture.
- **Background Sync** → Implemented with **WorkManager** to retry sync tasks when internet is available.

---

## 📶 Offline-First & Sync Logic

1. **Local First Write**  
   - All CRUD operations (add, update, delete student) are first performed locally in the Room database.
   - Each student has a `syncStatus` field (`PENDING`, `SYNCED`, `FAILED`) to track its sync state.

2. **Background Sync**  
   - A `SyncWorker` runs periodically (via WorkManager) or when triggered manually.  
   - It checks for all unsynced records (`syncStatus = PENDING`) and pushes them to the server.  
   - If the sync succeeds, the record is marked as `SYNCED`. Otherwise, it retries.

3. **Offline Availability**  
   - Since all data is stored locally in Room, the app works fully offline.  
   - When internet returns, WorkManager ensures background sync.

---

## ⚔️ Conflict Resolution Strategy

- Each student record has `createdAt` and `updatedAt` timestamps.  
- During sync, conflicts are resolved using **last-write-wins**:
  - If the local `updatedAt` > remote `updatedAt`, local changes overwrite server data.  
  - Otherwise, remote changes are applied locally.  
- This ensures minimal data loss while keeping updates consistent.

---

## 📚 Justification for Chosen Patterns & Libraries

- **Kotlin + Jetpack Compose** → Modern, declarative UI toolkit with better readability and maintainability.  
- **MVVM Architecture** → Clean separation of concerns and testable code.  
- **Room Database** → Robust ORM for local persistence, ideal for offline-first apps.  
- **WorkManager** → Best suited for guaranteed background tasks like sync, even after app restarts.  
- **Hilt (Dagger)** → Simplifies dependency injection, reduces boilerplate, and improves testability.  
- **Firebase** → As there no backend api to sync data so i used firebase service to make syncing working.

---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone [https://github.com/your-username/navgurukul-task](https://github.com/Udaytech1/NavgurukulTask/).git
