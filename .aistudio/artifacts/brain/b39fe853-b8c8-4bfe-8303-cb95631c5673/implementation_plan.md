# Implementation Plan - Comprehensive All-Around Milestone Journey System

Expand the Gm fitness gamification ecosystem with an **All-Around Milestone Journey** covering Strength PRs, Streak Consistency, Body Transformation, and Endurance/Cardio. The update adds a dedicated interactive visual roadmap tab with progress tracking, tier milestones, dynamic calculations from real user workout and physical data, and claimable rewards with celebratory modals.

---

## 1. Objectives & Scope
- **All-Around Milestone Engine**:
  - **Strength & Lifting**: 1RM milestones (Bench Press, Squats, Deadlift, Overhead Press) and Volume milestones (10 Tons, 50 Tons cumulative).
  - **Streaks & Consistency**: Habit milestones at 3, 7, 14, 30, 60, 100, and 365 days with streak shield bonuses.
  - **Body Transformation & Nutrition**: Weight delta milestones (1kg, 5kg, 10kg target reached), calorie adherence streaks, and macro tracking consistency.
  - **Endurance & Cardio Movement**: 5K, 10K, Half-Marathon distances, 100K and 500K cumulative steps.
- **Dedicated Milestone Journey Roadmap Tab**:
  - Seamless two-tab experience in `RewardsScreen.kt`:
    1. **مسار المحطات (Milestone Journey Roadmap)**: Visual interactive timeline with progress connecting nodes, tier badges (Bronze, Silver, Gold, Platinum, Diamond), category filter chips, and progress percentage.
    2. **الأوسمة والشعلة (Badges & Streak Engine)**: Flame tier visualizer, 7-day calendar dots, streak shields, and badge cabinet.
  - Interactive "Claim Milestone" action awarding bonus XP / points and triggering the animated `CelebrationModal`.
- **Deep Data & Hardware Telemetry Integration**:
  - Live linkage to Room Database tables (`PersonalRecordEntity`, `WeightProgressEntity`, `WorkoutLogEntity`, `DailyStepLogEntity`, `GpsTrackEntity`, `UserProfileEntity`).
  - Header shortcut integration in `GmAppTopHeader` to navigate directly to the Milestone Journey from any screen.
  - Quick-glance Milestone Widget on `HomeScreen.kt` highlighting the next nearest milestone.

---

## 2. Proposed Changes & Architecture

### Data Layer
#### [NEW] `app/src/main/java/com/example/data/MilestoneJourney.kt`
- Data models for:
  - `MilestoneCategory`: `STRENGTH`, `STREAK`, `TRANSFORMATION`, `ENDURANCE`.
  - `MilestoneTier`: `BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`.
  - `FitnessMilestone`: `id`, `category`, `tier`, `titleArabic`, `titleEnglish`, `descriptionArabic`, `targetMetric`, `currentMetric`, `unitArabic`, `xpReward`, `iconEmoji`, `isUnlocked`, `isClaimed`, `unlockedDate`.
  - `MilestoneCategoryProgress`: Total count, completed count, claimed count, overall percentage.
- Repository / Engine: `MilestoneEngine` with all predefined milestones across the 4 domains and progress calculation methods based on live user stats.

#### Database Persistence
- Update `UserProfileEntity` or `GmDatabase.kt` / `ClaimedMilestoneEntity` to store claimed milestone IDs persistently, preventing repeat reward claims across app restarts.

---

### UI Layer
#### [NEW] `app/src/main/java/com/example/ui/components/MilestoneJourneyRoadmapView.kt`
- Interactive visual roadmap:
  - Category selector chips (`الكل`, `القوة والأوزان`, `الاستمرارية`, `التحول والوزن`, `اللياقة والمسافات`).
  - Journey progress hero card: Displays category level, % completion, next unlockable milestone, and unclaimed rewards badge.
  - Vertical timeline nodes with connected progress lines, glowing active state, completed checkmark, lock status, and "استلام الجائزة (Claim)" button.
  - Milestone detail sheet when tapping any milestone node.

#### [MODIFY] `app/src/main/java/com/example/ui/screens/RewardsScreen.kt`
- Add top tab navigation:
  - Tab 1: **مسار المحطات والإنجازات (Milestone Journey)**.
  - Tab 2: **الأوسمة والشعلة (Badges & Streaks)**.
- Connect claim action to `viewModel.claimMilestoneReward(milestone)` which triggers `CelebrationModal`.

#### [MODIFY] `app/src/main/java/com/example/ui/GmMainViewModel.kt`
- StateFlow for `milestones`: Reactive computation from `personalRecords`, `weightEntries`, `workoutLogs`, `dailyStepLogs`, and `gpsTracks`.
- Methods:
  - `claimMilestone(milestoneId: String)`: Adds points/XP to `UserProfile`, marks milestone as claimed, and shows celebratory modal.
  - `getMilestoneCategoryProgress(category: MilestoneCategory)`: Computes completion %.

#### [MODIFY] `app/src/main/java/com/example/ui/components/CommonComponents.kt` & `MainActivity.kt`
- Make top header points and streak pills clickable to navigate directly to `NavigationScreen.REWARDS`.
- Add quick milestone preview card on `HomeScreen.kt`.

---

## 3. Verification & Testing Plan
- **Local Unit Testing**:
  - Add unit tests in `app/src/test/java/com/example/MilestoneEngineTest.kt` verifying correct milestone unlocking criteria for Strength PRs, streaks, weight targets, and step distance milestones.
  - Run `gradle :app:testDebugUnitTest`.
- **Build & Compilation Verification**:
  - Run `compile_applet` to ensure zero compilation or dependency errors.
  - Verify layout responsiveness, touch targets (>48dp), and RTL Arabic localization.
