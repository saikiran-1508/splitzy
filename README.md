# Splitzy

**Split Expenses. Not Friendships.**

An Android expense-splitting app for groups who share costs — housemates, trips, one-off events. Log what everyone spends, and Splitzy works out the shortest path to settling up.

Built solo in Kotlin with Jetpack Compose and Clean Architecture.

---

## What it does

- **Three kinds of group** — Home (a shared household, or just your own spending), Trip, or Event. Each seeds its own sensible categories on creation.
- **Log an expense** against a category, record who paid, and pick who it's split between.
- **Repeat an expense** in one tap — recurring spends like petrol carry over the category, payer and split, so you only type the new amount.
- **Settle up** — see both each person's net balance and the *minimum set of payments* that clears the group, then share a plain-text summary through any app.
- **Works offline.** Room is the source of truth; nothing on screen waits on a network call.
- **Add people from your contacts** — pick anyone in your phonebook; no email address required.
- **Share an invite link** (`splitzy://join/<id>`) through WhatsApp or any other app. See the caveat in [Not built yet](#not-built-yet).
- **Sign in** with email/password or Google.
- **Spending history** on your profile: this month, this year, the last six months, all time.

---

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 — no XML layouts anywhere |
| Architecture | Clean Architecture (3 layers) + MVVM |
| Local storage | Room 2.8.4 (Flow-backed DAOs) |
| Async / state | Coroutines, Flow, StateFlow |
| Dependency injection | Hilt 2.57.2 (KSP) |
| Navigation | Navigation Compose |
| Auth | Firebase Authentication + Credential Manager for Google sign-in |
| Networking | Retrofit + Moshi + OkHttp *(layer built; see [Not built yet](#not-built-yet))* |

Build: AGP 8.13.2 · Kotlin 2.0.21 · KSP · Compose BOM 2025.12.01 · minSdk 24 · target/compileSdk 36

---

## Architecture

Three layers with a strict dependency rule — **presentation and data both point inward at domain, and domain points at nothing.**

```
presentation ──►  domain  ◄── data
```

`domain/` is pure Kotlin. It has no Android imports at all, which means the business rules run on the JVM in milliseconds with no emulator.

```
com.example.splitzy/
├─ domain/                 pure Kotlin — no Android
│  ├─ model/               Expense, Group, Category, Balance, GroupType
│  ├─ repository/          interfaces only
│  └─ usecase/             18 use cases, one job each
├─ data/                   implements the domain's interfaces
│  ├─ entity/              Room @Entity classes
│  ├─ local/               database, DAOs, type converters
│  ├─ remote/              Retrofit service + DTOs
│  ├─ mapper/              Entity ↔ Domain ↔ DTO, never Entity ↔ DTO
│  └─ repository/          repository implementations
├─ presentation/           Compose UI + ViewModels
│  ├─ auth/ home/ expenses/ groups/ profile/ splash/
│  ├─ common/              shared UI helpers
│  └─ navigation/          NavHost, all routes in one place
├─ di/                     Hilt modules
└─ ui/theme/               colour, type, category styling
```

Hilt wires it together: `@Provides` for types the project doesn't own (Room, Retrofit, FirebaseAuth), `@Binds` for its own interface → implementation pairs.

---

## How state flows

There is no `loadData()` anywhere in this app. UI state is **derived**, not assigned:

1. A Room DAO returns `Flow<List<ExpenseEntity>>` and re-emits on any write to the table.
2. The repository maps entities to domain models — the domain never sees a Room type.
3. The ViewModel uses `flatMapLatest` to switch to a different group's flow (cancelling the previous one) and `combine` to merge expenses, categories, group and messages into one immutable `UiState`. Balances are computed here, never stored.
4. `stateIn(SharingStarted.WhileSubscribed(5_000))` keeps the chain alive only while a screen is watching — and through a rotation.
5. Compose collects with `collectAsStateWithLifecycle()`.

The practical result: **adding an expense writes one row and does nothing else.** Room re-emits, balances recompute, the screen updates itself. No callbacks, no manual refresh.

`LiveData` is deliberately not used — StateFlow is plain Kotlin, always holds a value, and composes with operators like `combine` and `flatMapLatest` that LiveData makes awkward.

---

## The settlement algorithm

Two steps, in `CalculateBalancesUseCase`:

**1. Net balances.** Walk every expense once, crediting the payer the full amount and debiting each participant their share. Result: one net figure per person. `O(n·m)`.

**2. Simplify.** Sort creditors and debtors by magnitude, then repeatedly settle the largest debtor against the largest creditor for `min(debt, credit)`, advancing whichever side reaches zero. A `0.01` tolerance absorbs floating-point drift.

This yields **at most n−1 transfers for n people** — four friends who'd otherwise owe each other six different ways settle in three payments.

Greedy isn't provably optimal (minimising transfers exactly is NP-hard, reducing to set partitioning), but it's fast and it's what production apps ship.

---

## Getting started

```bash
git clone https://github.com/saikiran-1508/splitzy.git
cd splitzy
```

**Firebase config is required.** `google-services.json` is deliberately not committed, so you'll need your own:

1. Create a project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.example.splitzy`
3. Under **Authentication → Sign-in method**, enable **Email/Password**. For Google sign-in, also enable **Google** and register your debug SHA-1.
4. Download `google-services.json` into `app/`

Then:

```bash
./gradlew assembleDebug
```

Without that file the app still builds and runs — auth is guarded, and it drops you straight into the app with sign-in disabled rather than crashing.

---

## Not built yet

Stated plainly, because the repo is public and the gaps are real:

- **No tests.** Only the generated Android Studio stubs. The use cases are pure Kotlin and very testable — this is the next thing to do.
- **No live backend.** The Retrofit layer, DTOs and interceptor are all written, but the base URL is a placeholder, so nothing syncs between devices. `syncWithRemote()` exists and is never called.
- **WorkManager, ML Kit and Firebase Cloud Messaging** appear in `build.gradle.kts` from earlier planning. **No code uses them.**
- **Destructive migrations.** Room runs with `fallbackToDestructiveMigration`, so a schema change wipes local data. Fine in development, must change before release.
- **Members are labels, not accounts.** A name or email identifies a person in a group, but nothing verifies it belongs to a real Splitzy user.
- **Invite links only open groups that already exist on the device.** The link, the `splitzy://` deep link handler and the join screen are all built and working — but with no backend, the recipient's phone has no copy of the group to join. Tapping an invite on another device shows a clear "this group isn't on this device" message rather than failing silently. Making invites work across phones is the backend's job.
- **Package is still `com.example.splitzy`** — needs a real application ID before any Play Store submission.

---

## Roadmap

1. Unit tests on the balance and settlement logic
2. A real backend (Firestore is the natural fit — its listeners map onto the existing Flow model, and Room stays as the offline cache)
3. Proper Room migrations
4. WorkManager to retry syncs that failed offline
5. Push notifications when someone adds an expense
6. Receipt scanning with ML Kit

---

## Licence

Not currently licensed for reuse. Built as a learning and portfolio project.
