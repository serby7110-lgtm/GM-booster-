package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppLanguage
import com.example.data.AppStrings
import com.example.data.BodyType
import com.example.data.CommunityComment
import com.example.data.CommunityPost
import com.example.data.CommunitySeed
import com.example.data.DayTrendStat
import com.example.data.Exercise
import com.example.data.FitnessGoal
import com.example.data.FitnessLevel
import com.example.data.GmRepository
import com.example.data.MealCategory
import com.example.data.Recipe
import com.example.data.RecipeDatabase
import com.example.data.RewardBadge
import com.example.data.RewardSystem
import com.example.data.StreakDetails
import com.example.data.FitnessMilestone
import com.example.data.MilestoneCategory
import com.example.data.MilestoneEngine
import com.example.data.MilestoneTier
import com.example.data.CommunityChallenge
import com.example.data.CommunityLeaderboardEngine
import com.example.data.LeaderboardMember
import com.example.data.WeeklyProgressSummary
import com.example.data.WorkoutCategory
import com.example.data.WorkoutDatabase
import com.example.data.WorkoutEnvironment
import com.example.data.CalorieGoalType
import com.example.data.ChallengesEngine
import com.example.data.ExercisePrCheckResult
import com.example.data.GeneratedWorkoutSession
import com.example.data.GeneratorDuration
import com.example.data.GeneratorEquipment
import com.example.data.GeneratorGoal
import com.example.data.GeneratorSplit
import com.example.data.MacroCalculationResult
import com.example.data.MacroCalculatorEngine
import com.example.data.MuscleGroup
import com.example.data.MuscleRecoveryAdvisor
import com.example.data.MuscleRecoveryState
import com.example.data.PersonalRecordsManager
import com.example.data.SmartWorkoutGeneratorEngine
import com.example.data.WeakPointDaySchedule
import com.example.data.WeakPointFocusEngine
import com.example.data.WeakPointFocusPlan
import com.example.data.database.DailyStepLogEntity
import com.example.data.database.GmDatabase
import com.example.data.database.GpsTrackEntity
import com.example.data.database.MealLogEntity
import com.example.data.database.PersonalRecordEntity
import com.example.data.database.StrengthLogEntity
import com.example.data.database.UserProfileEntity
import com.example.data.database.WaterLogEntity
import com.example.data.database.WeeklyChallengeEntity
import com.example.data.database.WeightProgressEntity
import com.example.data.database.WorkoutLogEntity
import com.example.data.database.ExerciseEntity
import com.example.data.database.WorkoutProgramEntity
import com.example.data.database.ProgramRoutineDayEntity
import com.example.data.ProgramGoal
import com.example.data.ProgramDifficulty
import com.example.data.ProgramDayExerciseItem
import com.example.data.ProgramRoutineDayItem
import com.example.data.ProgressiveOverloadAdvice
import com.example.data.ProgressiveOverloadEngine
import com.example.data.auth.SocialAccountManager
import com.example.data.auth.SocialAccountState
import com.example.data.location.GmLocationManager
import com.example.data.location.UserLocationInfo
import com.example.data.security.SecurityAuditReport
import com.example.data.security.SecurityManager
import com.example.data.tracking.GpsSessionType
import com.example.data.tracking.GpsTrackerEngine
import com.example.data.tracking.GpsTrackingSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.UUID

enum class NavigationScreen(
    val arabicTitle: String,
    val englishTitle: String,
    val indonesianTitle: String
) {
    HOME("الرئيسية", "Home", "Beranda"),
    WORKOUTS("التمارين", "Workouts", "Latihan"),
    GPS_TRACKER("تتبع GPS والخطوات", "GPS & Steps", "GPS & Langkah"),
    NUTRITION("التغذية والماء", "Nutrition & Water", "Nutrisi & Air"),
    EVOLUTION("تطور الجسم", "Body Evolution", "Evolusi Tubuh"),
    COMMUNITY("المجتمع", "Community", "Komunitas"),
    REWARDS("النقاط والمكافآت", "Rewards", "Hadiah"),
    SETTINGS("الإعدادات", "Settings", "Pengaturan");

    fun getTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> arabicTitle
        AppLanguage.ENGLISH -> englishTitle
        AppLanguage.INDONESIAN -> indonesianTitle
    }
}

data class ActiveWorkoutSession(
    val exercise: Exercise,
    val isRunning: Boolean = false,
    val elapsedSeconds: Int = 0,
    val currentSet: Int = 1,
    val totalSets: Int = 4,
    val currentWeightKg: Float = 40.0f,
    val currentReps: Int = 10,
    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 60,
    val isCompleted: Boolean = false,
    val recentPrResult: ExercisePrCheckResult? = null,
    val completedSetsHistory: List<Pair<Float, Int>> = emptyList(),
    val totalVolumeLiftedKg: Float = 0f,
    val overloadAdvice: ProgressiveOverloadAdvice? = null,
    val activeProgramTitle: String? = null,
    val activeDayTitle: String? = null,
    val routineExerciseIndex: Int = 0,
    val totalRoutineExercises: Int = 1
)

data class MacroTarget(
    val dailyCalories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

class GmMainViewModel(application: Application) : AndroidViewModel(application) {

    private val db: GmDatabase = GmDatabase.getDatabase(application)
    private val repository: GmRepository = GmRepository(db)

    // Location Support Manager
    val locationManager = GmLocationManager(application, viewModelScope)
    val userLocationInfo: StateFlow<UserLocationInfo> = locationManager.currentLocation

    // Social Account Linking Manager
    val socialAccountManager = SocialAccountManager(application)
    val socialAccountState: StateFlow<SocialAccountState> = socialAccountManager.accountState

    // Security & Anti-Tamper Armor Manager
    val securityManager = SecurityManager(application)
    val isAppLocked: StateFlow<Boolean> = securityManager.isAppLocked
    val failedAttempts: StateFlow<Int> = securityManager.failedAttempts
    val lockoutUntilTimestamp: StateFlow<Long> = securityManager.lockoutUntilTimestamp
    val securityAuditReport: StateFlow<SecurityAuditReport> = securityManager.auditReport

    // GPS & Step Tracking Engine
    val gpsTrackerEngine = GpsTrackerEngine(db, viewModelScope)
    val gpsSession: StateFlow<GpsTrackingSession> = gpsTrackerEngine.session
    val allGpsTracks: StateFlow<List<GpsTrackEntity>> = db.gpsTrackDao().getAllGpsTracks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val gpsTracks: StateFlow<List<GpsTrackEntity>> = allGpsTracks
    val allStepLogs: StateFlow<List<DailyStepLogEntity>> = db.dailyStepLogDao().getAllStepLogs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val userProfile: StateFlow<UserProfileEntity?>
    val userDailyStepLimit: StateFlow<Int>
    val userDailyDistanceLimitKm: StateFlow<Float>
    val workoutLogs: StateFlow<List<WorkoutLogEntity>>
    val weightEntries: StateFlow<List<WeightProgressEntity>>
    val todayMeals: StateFlow<List<MealLogEntity>>
    val caloriesBurnedToday: StateFlow<Int?>

    // Water Tracker Flows
    val todayWaterLogs: StateFlow<List<WaterLogEntity>>
    val allWaterLogs: StateFlow<List<WaterLogEntity>>
    val todayWaterTotalMl: StateFlow<Int?>

    // Personal Records and Strength Logs
    val personalRecords: StateFlow<List<PersonalRecordEntity>>
    val strengthLogs: StateFlow<List<StrengthLogEntity>>
    val weeklyChallenges: StateFlow<List<WeeklyChallengeEntity>>
    val muscleRecoveryStates: StateFlow<List<MuscleRecoveryState>>
    val allPrograms: StateFlow<List<WorkoutProgramEntity>>
    val activeProgram: StateFlow<WorkoutProgramEntity?>
    val catalogExercises: StateFlow<List<ExerciseEntity>>

    // Selected Program Detail modal state
    private val _selectedProgramIdForDetail = MutableStateFlow<String?>(null)
    val selectedProgramIdForDetail: StateFlow<String?> = _selectedProgramIdForDetail.asStateFlow()

    // Active Routine Day Execution Queue
    private var activeRoutineExerciseQueue: List<ProgramDayExerciseItem> = emptyList()
    private var currentRoutineDayEntity: ProgramRoutineDayEntity? = null
    private var currentRoutineProgramTitle: String? = null

    // Weak Point Focus State
    private val _selectedWeakPointMuscle = MutableStateFlow(MuscleGroup.LEGS_QUADS)
    val selectedWeakPointMuscle: StateFlow<MuscleGroup> = _selectedWeakPointMuscle.asStateFlow()

    private val _weakPointPlan = MutableStateFlow<WeakPointFocusPlan>(
        WeakPointFocusEngine.generatePlanForMuscle(MuscleGroup.LEGS_QUADS)
    )
    val weakPointPlan: StateFlow<WeakPointFocusPlan> = _weakPointPlan.asStateFlow()

    // Smart Workout Generator State
    private val _generatedSmartWorkout = MutableStateFlow<GeneratedWorkoutSession?>(null)
    val generatedSmartWorkout: StateFlow<GeneratedWorkoutSession?> = _generatedSmartWorkout.asStateFlow()

    // Exercise Detail modal state
    private val _selectedExerciseForDetail = MutableStateFlow<Exercise?>(null)
    val selectedExerciseForDetail: StateFlow<Exercise?> = _selectedExerciseForDetail.asStateFlow()

    private val _currentScreen = MutableStateFlow(NavigationScreen.HOME)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    // Multi-Language State (Arabic, English, Indonesian)
    private val _currentLanguage = MutableStateFlow(AppLanguage.ARABIC)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Workouts Filter State
    private val _selectedEnvironment = MutableStateFlow(WorkoutEnvironment.BOTH)
    val selectedEnvironment: StateFlow<WorkoutEnvironment> = _selectedEnvironment.asStateFlow()

    private val _selectedCategory = MutableStateFlow<WorkoutCategory?>(null)
    val selectedCategory: StateFlow<WorkoutCategory?> = _selectedCategory.asStateFlow()

    private val _filterByBodyTypeOnly = MutableStateFlow(false)
    val filterByBodyTypeOnly: StateFlow<Boolean> = _filterByBodyTypeOnly.asStateFlow()

    // Active Workout Player
    private val _activeSession = MutableStateFlow<ActiveWorkoutSession?>(null)
    val activeSession: StateFlow<ActiveWorkoutSession?> = _activeSession.asStateFlow()
    private var timerJob: Job? = null

    // Community Posts State
    private val _communityPosts = MutableStateFlow<List<CommunityPost>>(CommunitySeed.initialPosts)
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    // Fitness Tracker Device Live Sync State
    private val _isTrackerConnected = MutableStateFlow(true)
    val isTrackerConnected: StateFlow<Boolean> = _isTrackerConnected.asStateFlow()

    private val _liveSteps = MutableStateFlow(7420)
    val liveSteps: StateFlow<Int> = _liveSteps.asStateFlow()

    private val _liveActiveMinutes = MutableStateFlow(48)
    val liveActiveMinutes: StateFlow<Int> = _liveActiveMinutes.asStateFlow()

    // Selected Recipe Modal
    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

    // Selected Meal Category Filter
    private val _selectedMealCategory = MutableStateFlow<MealCategory?>(null)
    val selectedMealCategory: StateFlow<MealCategory?> = _selectedMealCategory.asStateFlow()

    // Toast / Feedback message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Streaks, Gamification & Community Challenges State
    private val _streakShieldActive = MutableStateFlow(true)
    val streakShieldActive: StateFlow<Boolean> = _streakShieldActive.asStateFlow()

    private val _selectedBadgeForDetail = MutableStateFlow<RewardBadge?>(null)
    val selectedBadgeForDetail: StateFlow<RewardBadge?> = _selectedBadgeForDetail.asStateFlow()

    private val _joinedChallengeIds = MutableStateFlow<Set<String>>(setOf("comm_ch_iron_streak_7", "comm_ch_century_sets_50"))
    val joinedChallengeIds: StateFlow<Set<String>> = _joinedChallengeIds.asStateFlow()

    private val _claimedChallengeIds = MutableStateFlow<Set<String>>(emptySet())
    val claimedChallengeIds: StateFlow<Set<String>> = _claimedChallengeIds.asStateFlow()

    private val _isLeaderboardWeekly = MutableStateFlow(true)
    val isLeaderboardWeekly: StateFlow<Boolean> = _isLeaderboardWeekly.asStateFlow()

    private val _celebrationRewardMessage = MutableStateFlow<String?>(null)
    val celebrationRewardMessage: StateFlow<String?> = _celebrationRewardMessage.asStateFlow()

    init {
        userProfile = repository.userProfileFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

        userDailyStepLimit = userProfile.map { it?.stepDailyLimit ?: 10000 }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            10000
        )

        userDailyDistanceLimitKm = userProfile.map { it?.distanceDailyLimitKm ?: 5.0f }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            5.0f
        )

        workoutLogs = repository.allWorkoutLogsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        weightEntries = repository.allWeightEntriesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        todayMeals = repository.getTodayMealsFlow().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        caloriesBurnedToday = repository.getCaloriesBurnedTodayFlow().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

        todayWaterLogs = repository.getTodayWaterLogsFlow().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allWaterLogs = repository.getAllWaterLogsFlow().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        todayWaterTotalMl = repository.getTodayWaterTotalMlFlow().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

        personalRecords = repository.allPersonalRecordsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        strengthLogs = repository.allStrengthLogsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        weeklyChallenges = repository.allChallengesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        muscleRecoveryStates = workoutLogs.map { logs ->
            MuscleRecoveryAdvisor.calculateRecoveryStates(logs)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MuscleRecoveryAdvisor.calculateRecoveryStates(emptyList())
        )

        allPrograms = repository.allProgramsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        activeProgram = repository.activeProgramFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        catalogExercises = repository.allCatalogExercisesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }

        // Initialize language from profile
        viewModelScope.launch {
            userProfile.collect { profile ->
                profile?.selectedLanguage?.let { langCode ->
                    _currentLanguage.value = AppLanguage.fromCode(langCode)
                }
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(selectedLanguage = lang.code))
            _snackbarMessage.value = AppStrings.get("language_changed_msg", lang)
        }
    }

    fun getString(key: String): String {
        return AppStrings.get(key, _currentLanguage.value)
    }

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
    }

    fun setEnvironmentFilter(env: WorkoutEnvironment) {
        _selectedEnvironment.value = env
    }

    fun setCategoryFilter(cat: WorkoutCategory?) {
        _selectedCategory.value = cat
    }

    fun setMealCategoryFilter(cat: MealCategory?) {
        _selectedMealCategory.value = cat
    }

    fun toggleBodyTypeFilter() {
        _filterByBodyTypeOnly.value = !_filterByBodyTypeOnly.value
    }

    fun setSelectedRecipe(recipe: Recipe?) {
        _selectedRecipe.value = recipe
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    // GPS & Step Tracking Handlers
    fun startGpsTracking(type: GpsSessionType) {
        val profile = userProfile.value
        val targetKm = profile?.distanceDailyLimitKm ?: 5.0f
        val targetSteps = profile?.stepDailyLimit ?: 10000
        gpsTrackerEngine.startSession(type, targetKm, targetSteps)
        _snackbarMessage.value = "بدأ تتبع ${type.titleArabic} مع تحديد الهدف: ${targetKm} كم 🚀"
    }

    fun pauseGpsTracking() {
        gpsTrackerEngine.pauseSession()
    }

    fun resumeGpsTracking() {
        gpsTrackerEngine.resumeSession()
    }

    fun finishAndSaveGpsTracking() {
        viewModelScope.launch {
            val savedTrack = gpsTrackerEngine.finishAndSaveSession()
            _liveSteps.value += savedTrack.stepCount
            _snackbarMessage.value = "تم حفظ جلسة GPS (${savedTrack.distanceKm} كم) بنجاح وحفظها محلياً 🏆"
        }
    }

    fun resetGpsTracking() {
        gpsTrackerEngine.resetSession()
    }

    fun updateDailyLimits(stepLimit: Int, distanceLimitKm: Float) {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    stepDailyLimit = stepLimit.coerceIn(1000, 50000),
                    distanceDailyLimitKm = distanceLimitKm.coerceIn(0.5f, 50.0f)
                )
            )
            _snackbarMessage.value = "تم تحديث هدف الخطوات ($stepLimit خطوة) والمسافة ($distanceLimitKm كم) بنجاح 🎯"
        }
    }

    // Body Type and Macro Calculations
    fun getBodyTypeEnum(): BodyType {
        val typeStr = userProfile.value?.bodyType ?: "ECTOMORPH"
        return try {
            BodyType.valueOf(typeStr)
        } catch (e: Exception) {
            BodyType.ECTOMORPH
        }
    }

    fun calculateMacros(): MacroTarget {
        val profile = userProfile.value ?: return MacroTarget(2300, 160, 260, 65)
        val bodyType = getBodyTypeEnum()
        val weight = profile.currentWeightKg
        val height = profile.heightCm
        val age = profile.age

        val bmr = (10 * weight) + (6.25f * height) - (5 * age) + (if (profile.gender == "ذكر") 5 else -161)
        val activityMult = 1.55f
        val tdee = bmr * activityMult
        val targetCalories = (tdee + bodyType.recommendedCalorieAdjustment).toInt().coerceAtLeast(1400)

        val proteinGrams = ((targetCalories * bodyType.proteinRatio) / 4).toInt()
        val carbsGrams = ((targetCalories * bodyType.carbRatio) / 4).toInt()
        val fatGrams = ((targetCalories * bodyType.fatRatio) / 9).toInt()

        return MacroTarget(targetCalories, proteinGrams, carbsGrams, fatGrams)
    }

    // Dynamic Daily Water Target Calculation (based on weight & activity)
    fun calculateDailyWaterGoalMl(): Int {
        val profile = userProfile.value ?: return 2500
        return repository.calculateDailyWaterGoalMl(profile.currentWeightKg, profile.activityLevel)
    }

    fun calculateDailyWaterGoalGlasses(): Int {
        return (calculateDailyWaterGoalMl() / 250).coerceAtLeast(6)
    }

    fun getFilteredExercises(): List<Exercise> {
        val currentBodyType = getBodyTypeEnum()
        val env = _selectedEnvironment.value
        val cat = _selectedCategory.value
        val onlyBodyType = _filterByBodyTypeOnly.value

        return WorkoutDatabase.allExercises.filter { exercise ->
            val matchesEnv = when (env) {
                WorkoutEnvironment.BOTH -> true
                WorkoutEnvironment.HOME -> exercise.environment == WorkoutEnvironment.HOME || exercise.environment == WorkoutEnvironment.BOTH
                WorkoutEnvironment.GYM -> exercise.environment == WorkoutEnvironment.GYM || exercise.environment == WorkoutEnvironment.BOTH
            }
            val matchesCat = cat == null || exercise.category == cat
            val matchesBodyType = !onlyBodyType || exercise.targetBodyTypes.contains(currentBodyType)

            matchesEnv && matchesCat && matchesBodyType
        }
    }

    fun getFilteredRecipes(): List<Recipe> {
        val currentBodyType = getBodyTypeEnum()
        val cat = _selectedMealCategory.value
        return RecipeDatabase.allRecipes.filter { recipe ->
            val matchesType = recipe.targetBodyType == currentBodyType || recipe.category == MealCategory.BUDGET || recipe.category == MealCategory.SMOOTHIE
            val matchesCat = cat == null || recipe.category == cat
            matchesType && matchesCat
        }
    }

    // Workout Player Control
    fun startWorkout(
        exercise: Exercise,
        initialSets: Int? = null,
        initialWeightKg: Float = 40.0f,
        initialReps: Int = 10,
        programTitle: String? = null,
        dayTitle: String? = null,
        routineIndex: Int = 0,
        totalRoutine: Int = 1
    ) {
        timerJob?.cancel()
        viewModelScope.launch {
            val overload = repository.getOverloadAdvice(
                exerciseId = exercise.id,
                exerciseNameArabic = exercise.nameArabic,
                targetRepRange = exercise.defaultReps
            )
            val startWeight = if (overload.suggestedWeightKg > 0f) overload.suggestedWeightKg else initialWeightKg
            val startReps = if (overload.suggestedReps > 0) overload.suggestedReps else initialReps

            _activeSession.value = ActiveWorkoutSession(
                exercise = exercise,
                isRunning = true,
                elapsedSeconds = 0,
                currentSet = 1,
                totalSets = initialSets ?: exercise.defaultSets,
                currentWeightKg = startWeight,
                currentReps = startReps,
                isResting = false,
                restSecondsRemaining = exercise.restDurationSeconds,
                isCompleted = false,
                recentPrResult = null,
                completedSetsHistory = emptyList(),
                totalVolumeLiftedKg = 0f,
                overloadAdvice = overload,
                activeProgramTitle = programTitle,
                activeDayTitle = dayTitle,
                routineExerciseIndex = routineIndex,
                totalRoutineExercises = totalRoutine
            )

            startWorkoutTimer()
        }
    }

    fun updateActiveWeightAndReps(weightKg: Float, reps: Int) {
        val session = _activeSession.value ?: return
        _activeSession.value = session.copy(
            currentWeightKg = weightKg.coerceIn(0f, 500f),
            currentReps = reps.coerceIn(1, 100)
        )
    }

    fun adjustRestTime(secondsDelta: Int) {
        val session = _activeSession.value ?: return
        if (session.isResting) {
            val updated = (session.restSecondsRemaining + secondsDelta).coerceAtLeast(5)
            _activeSession.value = session.copy(restSecondsRemaining = updated)
        }
    }

    private fun startWorkoutTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val session = _activeSession.value ?: break
                if (session.isRunning) {
                    if (session.isResting) {
                        if (session.restSecondsRemaining > 1) {
                            _activeSession.value = session.copy(
                                restSecondsRemaining = session.restSecondsRemaining - 1
                            )
                        } else {
                            val nextSet = session.currentSet + 1
                            if (nextSet > session.totalSets) {
                                completeActiveWorkout()
                            } else {
                                _activeSession.value = session.copy(
                                    currentSet = nextSet,
                                    isResting = false,
                                    restSecondsRemaining = session.exercise.restDurationSeconds,
                                    recentPrResult = null
                                )
                            }
                        }
                    } else {
                        _activeSession.value = session.copy(
                            elapsedSeconds = session.elapsedSeconds + 1
                        )
                    }
                }
            }
        }
    }

    fun pauseResumeWorkout() {
        val session = _activeSession.value ?: return
        _activeSession.value = session.copy(isRunning = !session.isRunning)
    }

    fun completeCurrentSet(weightKg: Float? = null, reps: Int? = null) {
        val session = _activeSession.value ?: return
        val currentWeight = weightKg ?: session.currentWeightKg
        val currentRepsCount = reps ?: session.currentReps

        viewModelScope.launch {
            val prResult = repository.logStrengthSet(
                exerciseId = session.exercise.id,
                exerciseNameArabic = session.exercise.nameArabic,
                setNumber = session.currentSet,
                weightKg = currentWeight,
                reps = currentRepsCount
            )

            if (prResult.isNewPr) {
                _snackbarMessage.value = "🔥 رقم قياسي جديد (PR) في ${session.exercise.nameArabic}! 1RM المقدر: ${prResult.estimated1RmKg} كجم (+${prResult.improvementPercent}%) 🏆"
            }

            val updatedHistory = session.completedSetsHistory + Pair(currentWeight, currentRepsCount)
            val updatedVolume = session.totalVolumeLiftedKg + (currentWeight * currentRepsCount)

            if (session.currentSet >= session.totalSets) {
                _activeSession.value = session.copy(
                    completedSetsHistory = updatedHistory,
                    totalVolumeLiftedKg = updatedVolume,
                    currentWeightKg = currentWeight,
                    currentReps = currentRepsCount,
                    recentPrResult = if (prResult.isNewPr) prResult else null
                )
                completeActiveWorkout()
            } else {
                _activeSession.value = session.copy(
                    isResting = true,
                    restSecondsRemaining = session.exercise.restDurationSeconds,
                    recentPrResult = if (prResult.isNewPr) prResult else null,
                    currentWeightKg = currentWeight,
                    currentReps = currentRepsCount,
                    completedSetsHistory = updatedHistory,
                    totalVolumeLiftedKg = updatedVolume
                )
            }
        }
    }

    fun skipRest() {
        val session = _activeSession.value ?: return
        val nextSet = session.currentSet + 1
        if (nextSet > session.totalSets) {
            completeActiveWorkout()
        } else {
            _activeSession.value = session.copy(
                currentSet = nextSet,
                isResting = false,
                restSecondsRemaining = session.exercise.restDurationSeconds,
                recentPrResult = null
            )
        }
    }

    fun completeActiveWorkout() {
        val session = _activeSession.value ?: return
        timerJob?.cancel()
        _activeSession.value = session.copy(isCompleted = true, isRunning = false)

        val durationMinutes = (session.elapsedSeconds / 60).coerceAtLeast(1)
        viewModelScope.launch {
            repository.logWorkoutCompletion(
                exercise = session.exercise,
                durationMinutes = durationMinutes,
                setsDone = session.totalSets
            )
            _liveSteps.value += 350
            _liveActiveMinutes.value += durationMinutes
            _snackbarMessage.value = "مبروك! حصدت ${session.exercise.difficultyRating} نقطة صعوبة وأكملت التمرين بنجاح 🔥"
        }
    }

    fun closeWorkoutPlayer() {
        timerJob?.cancel()
        _activeSession.value = null
    }

    // Program Routine Runner
    fun startProgramDayRoutine(day: ProgramRoutineDayEntity, programTitle: String) {
        viewModelScope.launch {
            val exerciseItems = ProgramDayExerciseItem.listFromJsonString(day.exercisesJson)

            if (exerciseItems.isEmpty()) {
                _snackbarMessage.value = "لا توجد تمارين محددة لهذا اليوم التدريبي"
                return@launch
            }

            activeRoutineExerciseQueue = exerciseItems
            currentRoutineDayEntity = day
            currentRoutineProgramTitle = programTitle

            loadAndLaunchRoutineExercise(0)
        }
    }

    fun advanceToNextRoutineExercise() {
        val session = _activeSession.value ?: return
        val nextIndex = session.routineExerciseIndex + 1
        if (nextIndex < activeRoutineExerciseQueue.size) {
            loadAndLaunchRoutineExercise(nextIndex)
        } else {
            closeWorkoutPlayer()
            _snackbarMessage.value = "🎉 كفو يا وحش! أتممت جميع تمارين ${currentRoutineDayEntity?.dayTitleArabic ?: "اليوم التدريبي"} بنجاح!"
        }
    }

    private fun loadAndLaunchRoutineExercise(index: Int) {
        if (index >= activeRoutineExerciseQueue.size) return
        val targetItem = activeRoutineExerciseQueue[index]
        val exercise = WorkoutDatabase.allExercises.find { it.id == targetItem.exerciseId }
            ?: Exercise(
                id = targetItem.exerciseId,
                nameArabic = targetItem.exerciseNameArabic,
                nameEnglish = targetItem.exerciseNameEnglish,
                nameIndonesian = targetItem.exerciseNameEnglish,
                category = WorkoutCategory.CHEST,
                environment = WorkoutEnvironment.GYM,
                difficultyRating = 2.0f,
                targetBodyTypes = listOf(BodyType.ECTOMORPH, BodyType.MESOMORPH, BodyType.ENDOMORPH),
                defaultSets = targetItem.targetSets,
                defaultReps = targetItem.repRange,
                restDurationSeconds = targetItem.restSeconds,
                estimatedCaloriesBurned = 140,
                equipmentArabic = targetItem.equipmentArabic,
                equipmentEnglish = "Gym Equipment",
                equipmentIndonesian = "Peralatan Gym",
                instructionsArabic = "قم بأداء ${targetItem.targetSets} مجموعات بتكرار ${targetItem.repRange} وتركيز على التكنيك الصحيح.",
                instructionsEnglish = "Perform sets with proper form.",
                instructionsIndonesian = "Lakukan set dengan teknik yang benar.",
                tipArabic = targetItem.notes.ifEmpty { "حافظ على التركيز العقلي العضلي" },
                tipEnglish = "Focus on mind-muscle connection",
                tipIndonesian = "Fokus pada kontraksi otot",
                targetMusclesArabic = listOf("عضلات مستهدفة"),
                commonMistakesArabic = listOf("تجنب التأرجح أو استخدام أوزان تؤثر على سلامة المفصل.")
            )

        startWorkout(
            exercise = exercise,
            initialSets = targetItem.targetSets,
            initialWeightKg = 40f,
            initialReps = 10,
            programTitle = currentRoutineProgramTitle,
            dayTitle = currentRoutineDayEntity?.dayTitleArabic,
            routineIndex = index,
            totalRoutine = activeRoutineExerciseQueue.size
        )
    }

    // Program Details & CRUD
    fun openProgramDetails(programId: String) {
        _selectedProgramIdForDetail.value = programId
    }

    fun closeProgramDetails() {
        _selectedProgramIdForDetail.value = null
    }

    fun setActiveProgram(programId: String) {
        viewModelScope.launch {
            repository.setActiveProgram(programId)
            _snackbarMessage.value = "تم تفعيل البرنامج كجدولك التدريبي الحالي 🏆"
        }
    }

    fun deleteProgram(programId: String) {
        viewModelScope.launch {
            repository.deleteProgram(programId)
            _snackbarMessage.value = "تم حذف البرنامج بنجاح"
        }
    }

    fun getProgramDaysFlow(programId: String) = repository.getRoutineDaysForProgram(programId)

    fun createCustomProgram(
        titleArabic: String,
        titleEnglish: String,
        descriptionArabic: String,
        goal: ProgramGoal,
        durationWeeks: Int,
        daysPerWeek: Int,
        difficulty: ProgramDifficulty,
        routineDays: List<ProgramRoutineDayItem>
    ) {
        viewModelScope.launch {
            val programId = "custom_prog_${UUID.randomUUID()}"
            val program = WorkoutProgramEntity(
                id = programId,
                titleArabic = titleArabic,
                titleEnglish = titleEnglish,
                descriptionArabic = descriptionArabic,
                goal = goal.name,
                durationWeeks = durationWeeks,
                daysPerWeek = daysPerWeek,
                difficultyLevel = difficulty.name,
                isCustom = true,
                iconBadge = goal.badgeIcon,
                isCurrentActiveProgram = false
            )

            val dayEntities = routineDays.mapIndexed { idx, day ->
                ProgramRoutineDayEntity(
                    id = "day_${UUID.randomUUID()}",
                    programId = programId,
                    dayNumber = idx + 1,
                    dayTitleArabic = day.dayTitleArabic,
                    dayTitleEnglish = day.dayTitleEnglish,
                    targetMusclesSummary = day.targetMusclesSummary,
                    exercisesJson = ProgramDayExerciseItem.listToJsonString(day.exercises),
                    estimatedDurationMinutes = day.estimatedDurationMinutes
                )
            }

            repository.saveCustomProgram(program, dayEntities)
            _snackbarMessage.value = "تم حفظ برنامجك التدريبي المخصص ($titleArabic) بنجاح! 💪"
        }
    }

    fun addCustomExercise(
        nameArabic: String,
        nameEnglish: String,
        category: WorkoutCategory,
        equipment: String,
        defaultSets: Int,
        defaultReps: String,
        restSeconds: Int,
        instructions: String,
        mistakes: List<String>
    ) {
        viewModelScope.launch {
            val exerciseId = "custom_ex_${UUID.randomUUID()}"
            val mistakesArray = JSONArray(mistakes)
            val entity = ExerciseEntity(
                id = exerciseId,
                nameArabic = nameArabic,
                nameEnglish = nameEnglish,
                targetMuscle = category.name,
                secondaryMusclesJson = "[]",
                equipmentRequired = equipment,
                difficultyLevel = "INTERMEDIATE",
                instructionsArabic = instructions,
                instructionsEnglish = "",
                commonMistakesArabicJson = mistakesArray.toString(),
                category = category.name,
                environment = "GYM",
                defaultSets = defaultSets,
                defaultReps = defaultReps,
                restDurationSeconds = restSeconds,
                estimatedCaloriesBurned = 130,
                isCustom = true
            )
            repository.addCustomExercise(entity)
            _snackbarMessage.value = "تمت إضافة التمرين المخصص ($nameArabic) إلى قاعدة التمارين! 🏋️‍♂️"
        }
    }

    // Exercise details dialog
    fun openExerciseDetails(exercise: Exercise) {
        _selectedExerciseForDetail.value = exercise
    }

    fun closeExerciseDetails() {
        _selectedExerciseForDetail.value = null
    }

    // Weak Point Focus Actions
    fun setWeakPointMuscle(muscle: MuscleGroup) {
        _selectedWeakPointMuscle.value = muscle
        _weakPointPlan.value = WeakPointFocusEngine.generatePlanForMuscle(muscle)
        viewModelScope.launch {
            repository.updateWeakPoints(muscle.name)
            _snackbarMessage.value = "تم توليد جدول استهداف نقطة الضعف (${muscle.arabicName}) بنجاح 🎯"
        }
    }

    // Smart Workout Generator Actions
    fun generateSmartWorkout(
        equipment: GeneratorEquipment,
        duration: GeneratorDuration,
        split: GeneratorSplit,
        goal: GeneratorGoal
    ) {
        val bodyType = getBodyTypeEnum()
        val generated = SmartWorkoutGeneratorEngine.generateWorkout(
            equipment = equipment,
            duration = duration,
            split = split,
            goal = goal,
            userBodyType = bodyType
        )
        _generatedSmartWorkout.value = generated
        _snackbarMessage.value = "تم توليد جلسة ${generated.titleArabic} وجاهزة للبدء 🚀"
    }

    fun clearGeneratedWorkout() {
        _generatedSmartWorkout.value = null
    }

    // Weekly Challenges Actions
    fun claimChallengeReward(challenge: WeeklyChallengeEntity) {
        if (!challenge.isCompleted || challenge.isClaimed) return
        viewModelScope.launch {
            repository.claimChallengeReward(challenge.challengeId, challenge.rewardPoints)
            _snackbarMessage.value = "تهانينا! حصلت على +${challenge.rewardPoints} نقطة مكافأة 🎁"
        }
    }

    // Custom Macro Calculator Actions
    fun calculateMacrosCustom(
        gender: String,
        weightKg: Float,
        heightCm: Float,
        age: Int,
        activityLevel: String,
        goalType: CalorieGoalType
    ): MacroCalculationResult {
        return MacroCalculatorEngine.calculate(
            gender = gender,
            weightKg = weightKg,
            heightCm = heightCm,
            age = age,
            activityLevel = activityLevel,
            goalType = goalType
        )
    }

    fun applyCustomMacros(result: MacroCalculationResult) {
        viewModelScope.launch {
            repository.updateMacroGoals(
                calories = result.targetCalories,
                protein = result.proteinGrams,
                carbs = result.carbsGrams,
                fats = result.fatGrams
            )
            _snackbarMessage.value = "تم تحديث أهداف الماكروز والسعرات اليومية (${result.targetCalories} سعرة) بنجاح 🥑"
        }
    }

    fun saveManualPersonalRecord(
        exerciseId: String,
        exerciseNameArabic: String,
        weightKg: Float,
        reps: Int
    ) {
        viewModelScope.launch {
            val estimated1Rm = PersonalRecordsManager.calculateOneRepMax(weightKg, reps)
            val record = PersonalRecordEntity(
                exerciseId = exerciseId,
                exerciseNameArabic = exerciseNameArabic,
                maxWeightKg = weightKg,
                maxReps = reps,
                estimatedOneRepMaxKg = estimated1Rm,
                previousMaxWeightKg = 0f,
                achievedTimestamp = System.currentTimeMillis()
            )
            repository.savePersonalRecord(record)
            _snackbarMessage.value = "تم حفظ الرقم القياسي الجديد لـ ($exerciseNameArabic) بنجاح 🏆"
        }
    }

    // Nutrition actions
    fun addMeal(name: String, category: String, calories: Int, protein: Int, carbs: Int, fats: Int) {
        viewModelScope.launch {
            repository.addMeal(
                MealLogEntity(
                    mealNameArabic = name,
                    mealCategory = category,
                    calories = calories,
                    proteinGrams = protein,
                    carbsGrams = carbs,
                    fatGrams = fats,
                    isSynced = false
                )
            )
            _snackbarMessage.value = "تم تسجيل الوجبة ($name) بنجاح 🥗"
        }
    }

    fun deleteMeal(id: Long) {
        viewModelScope.launch {
            repository.deleteMeal(id)
            _snackbarMessage.value = "تم حذف الوجبة."
        }
    }

    // Water Tracking Actions
    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.logWaterIntake(amountMl)
            _snackbarMessage.value = "تم تسجيل +${amountMl} مل (${String.format(java.util.Locale.US, "%.2f", amountMl / 1000f)} لتر) ماء 💧"
        }
    }

    fun removeWater(amountMl: Int) {
        viewModelScope.launch {
            val logs = repository.getTodayWaterLogsFlow().stateIn(viewModelScope).value
            if (logs.isNotEmpty()) {
                val latest = logs.first()
                repository.deleteWaterLog(latest.id, latest.amountMl)
                _snackbarMessage.value = "تم التراجع عن آخر تسجيل للماء 💧"
            } else {
                decrementWater()
            }
        }
    }

    fun deleteWaterLog(id: Long, amountMl: Int) {
        viewModelScope.launch {
            repository.deleteWaterLog(id, amountMl)
        }
    }

    fun incrementWater() {
        logWater(250)
    }

    // Security & App Lock Actions
    fun verifyPinCode(pin: String): Boolean {
        val currentHash = userProfile.value?.pinCodeHash ?: "1234"
        return securityManager.verifyPin(pin, currentHash)
    }

    fun unlockBiometrics() {
        securityManager.unlockWithBiometrics()
    }

    fun lockApp() {
        securityManager.lockApp()
    }

    fun togglePinLock(enabled: Boolean) {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isAppLockEnabled = enabled))
            securityManager.runFullSecurityAudit(enabled, current.isBiometricEnabled, current.isScreenShieldEnabled)
            _snackbarMessage.value = if (enabled) "تم تفعيل حماية القفل بالـ PIN 🔒" else "تم إلغاء قفل الـ PIN"
        }
    }

    fun toggleBiometricLock(enabled: Boolean) {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isBiometricEnabled = enabled))
            securityManager.runFullSecurityAudit(current.isAppLockEnabled, enabled, current.isScreenShieldEnabled)
            _snackbarMessage.value = if (enabled) "تم تفعيل فتح القفل بالبصمة البيومترية 🛡️" else "تم إلغاء البصمة"
        }
    }

    fun toggleScreenShield(enabled: Boolean) {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isScreenShieldEnabled = enabled))
            securityManager.runFullSecurityAudit(current.isAppLockEnabled, current.isBiometricEnabled, enabled)
            _snackbarMessage.value = if (enabled) "تم تفعيل درع خصوصية الشاشة (منع التصوير) 🛡️" else "تم إيقاف درع خصوصية الشاشة"
        }
    }

    fun changePinCode(newPin: String) {
        val current = userProfile.value ?: return
        val hashed = securityManager.hashSha256(newPin)
        viewModelScope.launch {
            repository.updateProfile(current.copy(pinCodeHash = hashed, isAppLockEnabled = true))
            _snackbarMessage.value = "تم تغيير رمز الـ PIN المشفر بنجاح ✅"
        }
    }

    fun runSecurityScan() {
        val current = userProfile.value
        securityManager.runFullSecurityAudit(
            isPinActive = current?.isAppLockEnabled ?: false,
            isBiometricActive = current?.isBiometricEnabled ?: false,
            isScreenShieldActive = current?.isScreenShieldEnabled ?: false
        )
        _snackbarMessage.value = "تم فحص أمان النظام بنجاح - التقييم (${securityAuditReport.value.overallScore}/100) 🛡️"
    }

    // Social Accounts Linking Actions
    fun linkGoogleAccount(email: String) {
        socialAccountManager.linkGoogle(email)
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isGoogleLinked = true, googleEmail = email))
            _snackbarMessage.value = "تم ربط حساب Google ($email) بنجاح 🌐"
        }
    }

    fun unlinkGoogleAccount() {
        socialAccountManager.unlinkGoogle()
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isGoogleLinked = false))
            _snackbarMessage.value = "تم إلغاء ربط حساب Google"
        }
    }

    fun linkFacebookAccount(name: String) {
        socialAccountManager.linkFacebook(name)
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isFacebookLinked = true, facebookName = name))
            _snackbarMessage.value = "تم ربط حساب Facebook بنجاح 📘"
        }
    }

    fun unlinkFacebookAccount() {
        socialAccountManager.unlinkFacebook()
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isFacebookLinked = false, facebookName = ""))
            _snackbarMessage.value = "تم إلغاء ربط حساب Facebook"
        }
    }

    fun linkInstagramAccount(handle: String) {
        socialAccountManager.linkInstagram(handle)
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isInstagramLinked = true, instagramHandle = handle))
            _snackbarMessage.value = "تم ربط حساب Instagram ($handle) بنجاح 📸"
        }
    }

    fun unlinkInstagramAccount() {
        socialAccountManager.unlinkInstagram()
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(current.copy(isInstagramLinked = false, instagramHandle = ""))
            _snackbarMessage.value = "تم إلغاء ربط حساب Instagram"
        }
    }

    // Location Support
    fun refreshLocation() {
        locationManager.checkPermissionAndStartUpdates()
    }

    fun decrementWater() {
        val current = userProfile.value?.waterGlasses ?: 0
        viewModelScope.launch {
            repository.updateWaterIntake((current - 1).coerceAtLeast(0))
        }
    }

    // Weight & Body Progress
    fun addWeightEntry(weight: Float, waist: Float?, chest: Float?, arm: Float?, note: String, photoUri: String?) {
        viewModelScope.launch {
            repository.addWeightProgress(
                WeightProgressEntity(
                    weightKg = weight,
                    waistCm = waist,
                    chestCm = chest,
                    armCm = arm,
                    photoUri = photoUri,
                    noteArabic = note,
                    isSynced = false
                )
            )
            _snackbarMessage.value = "تم تسجيل قياس الوزن وتحديث منحنى التطور 📈"
        }
    }

    fun deleteWeightEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteWeightProgress(id)
        }
    }

    // Community Actions
    fun togglePostLike(postId: String) {
        val currentList = _communityPosts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = currentList[index]
            val newLiked = !post.isLikedByMe
            val newLikes = if (newLiked) post.likesCount + 1 else post.likesCount - 1
            currentList[index] = post.copy(
                isLikedByMe = newLiked,
                likesCount = newLikes.coerceAtLeast(0)
            )
            _communityPosts.value = currentList
        }
    }

    fun addCommentToPost(postId: String, commentText: String) {
        if (commentText.isBlank()) return
        val currentList = _communityPosts.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = currentList[index]
            val newComments = post.comments + CommunityComment(
                id = UUID.randomUUID().toString(),
                authorName = userProfile.value?.name ?: "بطل Gm",
                textArabic = commentText,
                timeAgoArabic = "الآن"
            )
            currentList[index] = post.copy(comments = newComments)
            _communityPosts.value = currentList
            _snackbarMessage.value = "تمت إضافة تعليقك وتشجيع البطل 💬"
        }
    }

    fun publishCommunityPost(content: String, workoutTag: String?) {
        if (content.isBlank()) return
        val profile = userProfile.value
        val level = RewardSystem.getCurrentLevel(profile?.totalPoints ?: 0f)
        val bodyType = getBodyTypeEnum()

        val newPost = CommunityPost(
            id = UUID.randomUUID().toString(),
            authorName = profile?.name ?: "بطل Gm",
            authorBodyType = bodyType,
            authorLevelName = level.nameArabic,
            contentArabic = content,
            workoutTagArabic = workoutTag,
            pointsGained = 2.0f,
            likesCount = 1,
            isLikedByMe = true,
            timeAgoArabic = "الآن"
        )
        _communityPosts.value = listOf(newPost) + _communityPosts.value
        _snackbarMessage.value = "تم نشر إنجازك في مجتمع Gm بنجاح 🚀"
    }

    // Profile & Settings
    fun updateProfileInfo(
        name: String,
        gender: String,
        age: Int,
        height: Float,
        targetWeight: Float,
        bodyType: BodyType,
        fitnessGoal: FitnessGoal,
        workoutReminders: Boolean,
        waterReminders: Boolean,
        reminderHour: Int,
        reminderMinute: Int
    ) {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    name = name,
                    gender = gender,
                    age = age,
                    heightCm = height,
                    targetWeightKg = targetWeight,
                    bodyType = bodyType.name,
                    fitnessGoal = fitnessGoal.name,
                    workoutRemindersEnabled = workoutReminders,
                    waterRemindersEnabled = waterReminders,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute
                )
            )
            _snackbarMessage.value = "تم حفظ إعدادات الملف الشخصي ونوع الجسم بنجاح ✅"
        }
    }

    fun toggleTrackerConnection() {
        _isTrackerConnected.value = !_isTrackerConnected.value
        if (_isTrackerConnected.value) {
            _snackbarMessage.value = "تم الاتصال بساعة اللياقة الذكية ومزامنة الخطوات ⌚"
        } else {
            _snackbarMessage.value = "تم إيقاف المزامنة المباشرة مع ساعة اللياقة."
        }
    }

    fun setLeaderboardFilter(isWeekly: Boolean) {
        _isLeaderboardWeekly.value = isWeekly
    }

    fun toggleStreakShield() {
        val current = _streakShieldActive.value
        _streakShieldActive.value = !current
        _snackbarMessage.value = if (!current) "تم تفعيل درع حماية السلسلة (Streak Shield) ليوم الراحة 🛡️" else "تم إيقاف درع حماية السلسلة."
    }

    fun selectBadgeForDetail(badge: RewardBadge?) {
        _selectedBadgeForDetail.value = badge
    }

    fun dismissCelebrationModal() {
        _celebrationRewardMessage.value = null
    }

    fun toggleJoinChallenge(challengeId: String) {
        val current = _joinedChallengeIds.value.toMutableSet()
        if (current.contains(challengeId)) {
            current.remove(challengeId)
            _snackbarMessage.value = "تمت مغادرة التحدي."
        } else {
            current.add(challengeId)
            _snackbarMessage.value = "أهلاً بك في التحدي! شعلة الحماس بدأت الآن 🚀🔥"
        }
        _joinedChallengeIds.value = current
    }

    fun claimCommunityChallenge(challenge: CommunityChallenge) {
        if (_claimedChallengeIds.value.contains(challenge.id)) return
        val currentClaimed = _claimedChallengeIds.value + challenge.id
        _claimedChallengeIds.value = currentClaimed

        val currentProfile = userProfile.value ?: return
        val updatedPoints = currentProfile.totalPoints + challenge.rewardPoints
        viewModelScope.launch {
            repository.updateProfile(currentProfile.copy(totalPoints = updatedPoints))
            _celebrationRewardMessage.value = "ألف مبروك يا بطل! تم حصد +${challenge.rewardPoints} نقطة صعوبة لإنجازك في ${challenge.titleArabic} 💎🏆"
        }
    }

    fun getStreakDetails(): StreakDetails {
        val streak = userProfile.value?.streakDays ?: 4
        return RewardSystem.calculateStreakDetails(streak, _streakShieldActive.value)
    }

    fun getCommunityChallenges(): List<CommunityChallenge> {
        val streak = userProfile.value?.streakDays ?: 4
        val workouts = workoutLogs.value.size.coerceAtLeast(3)
        val points = userProfile.value?.totalPoints ?: 12.4f
        return CommunityLeaderboardEngine.getDefaultChallenges(
            joinedIds = _joinedChallengeIds.value,
            claimedIds = _claimedChallengeIds.value,
            currentStreak = streak,
            workoutsCount = workouts,
            totalPoints = points
        )
    }

    fun getLeaderboard(): List<LeaderboardMember> {
        val profile = userProfile.value
        val points = profile?.totalPoints ?: 12.4f
        val name = profile?.name ?: "بطل Gm"
        val streak = profile?.streakDays ?: 4
        val bodyType = getBodyTypeEnum()
        return CommunityLeaderboardEngine.getLeaderboard(
            currentUserPoints = points,
            currentUserName = name,
            currentUserStreak = streak,
            currentUserBodyType = bodyType,
            isWeekly = _isLeaderboardWeekly.value
        )
    }

    fun getUnlockedBadges(): List<RewardBadge> {
        val points = userProfile.value?.totalPoints ?: 0f
        val streak = userProfile.value?.streakDays ?: 0
        val workoutsCount = workoutLogs.value.size
        val prsCount = personalRecords.value.size

        return RewardSystem.defaultBadges.map { badge ->
            val isUnlocked: Boolean
            val currentProg: Float
            val maxProg: Float
            val label: String

            when (badge.id) {
                "badge_first_workout" -> {
                    isUnlocked = workoutsCount >= 1 || points >= 1f
                    currentProg = if (isUnlocked) 1f else 0f
                    maxProg = 1f
                    label = if (isUnlocked) "1 / 1 تمرين" else "0 / 1 تمرين"
                }
                "badge_streak_3" -> {
                    isUnlocked = streak >= 3
                    currentProg = streak.coerceAtMost(3).toFloat()
                    maxProg = 3f
                    label = "$streak / 3 أيام"
                }
                "badge_streak_7" -> {
                    isUnlocked = streak >= 7
                    currentProg = streak.coerceAtMost(7).toFloat()
                    maxProg = 7f
                    label = "$streak / 7 أيام"
                }
                "badge_streak_30" -> {
                    isUnlocked = streak >= 30
                    currentProg = streak.coerceAtMost(30).toFloat()
                    maxProg = 30f
                    label = "$streak / 30 يوماً"
                }
                "badge_century_club" -> {
                    val sets = (workoutsCount * 4).coerceAtLeast(16)
                    isUnlocked = sets >= 100
                    currentProg = sets.coerceAtMost(100).toFloat()
                    maxProg = 100f
                    label = "$sets / 100 جولة"
                }
                "badge_point_collector_10" -> {
                    isUnlocked = points >= 10.0f
                    currentProg = points.coerceAtMost(10f)
                    maxProg = 10f
                    label = "%.1f / 10 نقاط".format(points)
                }
                "badge_point_collector_50" -> {
                    isUnlocked = points >= 50.0f
                    currentProg = points.coerceAtMost(50f)
                    maxProg = 50f
                    label = "%.1f / 50 نقطة".format(points)
                }
                "badge_volume_titan" -> {
                    val volume = ((workoutsCount * 1800) + (points.toInt() * 150)).coerceIn(1500, 10000)
                    isUnlocked = volume >= 10000
                    currentProg = volume.toFloat()
                    maxProg = 10000f
                    label = "$volume / 10,000 كجم"
                }
                "badge_pr_crusher" -> {
                    val count = prsCount.coerceAtLeast(1)
                    isUnlocked = count >= 3
                    currentProg = count.coerceAtMost(3).toFloat()
                    maxProg = 3f
                    label = "$count / 3 أرقام"
                }
                "badge_max_difficulty" -> {
                    isUnlocked = workoutLogs.value.any { it.difficultyPoints >= 2.8f } || points >= 8f
                    currentProg = if (isUnlocked) 3f else 1f
                    maxProg = 3f
                    label = if (isUnlocked) "مكتمل بنجاح" else "بانتظار تمرين 3.0"
                }
                "badge_nutrition_master" -> {
                    val waters = todayWaterLogs.value.size + todayMeals.value.size
                    isUnlocked = waters >= 5 || points >= 15f
                    currentProg = waters.coerceAtMost(15).toFloat()
                    maxProg = 15f
                    label = "$waters / 15 تسجيلاً"
                }
                "badge_early_bird" -> {
                    isUnlocked = true
                    currentProg = 1f
                    maxProg = 1f
                    label = "مكتمل بنجاح"
                }
                else -> {
                    isUnlocked = points >= badge.requiredPoints
                    currentProg = points.coerceAtMost(badge.requiredPoints)
                    maxProg = badge.requiredPoints
                    label = "%.1f / %.1f".format(points, badge.requiredPoints)
                }
            }
            badge.copy(
                isUnlocked = isUnlocked,
                currentProgress = currentProg,
                maxProgress = maxProg,
                progressLabelArabic = label,
                unlockedDate = if (isUnlocked) (badge.unlockedDate ?: "مكتمل") else null
            )
        }
    }

    fun getMilestones(): List<FitnessMilestone> {
        val profile = userProfile.value
        val records = personalRecords.value
        val streak = profile?.streakDays ?: 4
        val currentWeight = profile?.currentWeightKg ?: 74.5f
        val targetWeight = profile?.targetWeightKg ?: 78.0f
        val initialWeight = weightEntries.value.lastOrNull()?.weightKg ?: currentWeight
        val waterGlasses = profile?.waterGlasses ?: 5
        val weightLogs = weightEntries.value
        val workouts = workoutLogs.value
        val gps = gpsTracks.value
        val steps = allStepLogs.value
        val claimedIds = profile?.getClaimedMilestonesSet() ?: emptySet()

        return MilestoneEngine.evaluateMilestones(
            personalRecords = records,
            streakDays = streak,
            currentWeightKg = currentWeight,
            targetWeightKg = targetWeight,
            initialWeightKg = initialWeight,
            waterGlasses = waterGlasses,
            weightLogs = weightLogs,
            workoutLogs = workouts,
            gpsTracks = gps,
            dailyStepLogs = steps,
            claimedIds = claimedIds
        )
    }

    fun claimMilestone(milestone: FitnessMilestone) {
        val profile = userProfile.value ?: return
        val currentClaimed = profile.getClaimedMilestonesSet()
        if (currentClaimed.contains(milestone.id)) return

        val newClaimedSet = currentClaimed + milestone.id
        val newClaimedString = newClaimedSet.joinToString(",")
        val updatedPoints = profile.totalPoints + milestone.xpReward

        viewModelScope.launch {
            repository.updateProfile(
                profile.copy(
                    totalPoints = updatedPoints,
                    claimedMilestonesString = newClaimedString
                )
            )
            _celebrationRewardMessage.value = "ألف مبروك يا بطل! حققت محطة ${milestone.titleArabic} وحصلت على +${milestone.xpReward} XP 🏆✨"
        }
    }

    fun getWeeklyTrendsSummary(): WeeklyProgressSummary {
        val profile = userProfile.value
        val allWeights = weightEntries.value
        val allWater = allWaterLogs.value
        val allWorkouts = workoutLogs.value
        val dailyWaterGoal = calculateDailyWaterGoalMl()
        val dailyCalorieBurnGoal = 400

        val dayStats = mutableListOf<DayTrendStat>()
        val dayNamesAr = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        val dayNamesEn = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val dayNamesId = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

        val sdf = java.text.SimpleDateFormat("d MMM", java.util.Locale.US)

        for (i in 6 downTo 0) {
            val cal = java.util.Calendar.getInstance().apply {
                add(java.util.Calendar.DAY_OF_YEAR, -i)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000L - 1
            val dayOfWeek = (cal.get(java.util.Calendar.DAY_OF_WEEK) - 1).coerceIn(0, 6)

            val dayWater = allWater.filter { it.dateTimestamp in startOfDay..endOfDay }.sumOf { it.amountMl }
            val dayWorkouts = allWorkouts.filter { it.dateTimestamp in startOfDay..endOfDay }
            val dayWorkoutCalories = dayWorkouts.sumOf { it.caloriesBurned }
            val dayActiveMovementCal = if (i == 0) (_liveSteps.value * 0.04).toInt() else (180 + (i * 45) % 150)
            val totalDayCaloriesBurned = dayWorkoutCalories + dayActiveMovementCal

            // Closest recorded weight on or before this day
            val weightForDay = allWeights.filter { it.dateTimestamp <= endOfDay }.maxByOrNull { it.dateTimestamp }?.weightKg
                ?: profile?.currentWeightKg ?: 73.5f

            dayStats.add(
                DayTrendStat(
                    dayIndex = 6 - i,
                    dayNameShortAr = dayNamesAr[dayOfWeek],
                    dayNameShortEn = dayNamesEn[dayOfWeek],
                    dayNameShortId = dayNamesId[dayOfWeek],
                    dateFormatted = sdf.format(java.util.Date(startOfDay)),
                    timestamp = startOfDay,
                    recordedWeightKg = weightForDay,
                    waterIntakeMl = dayWater,
                    dailyWaterGoalMl = dailyWaterGoal,
                    caloriesBurned = totalDayCaloriesBurned,
                    dailyCalorieGoal = dailyCalorieBurnGoal,
                    workoutCount = dayWorkouts.size,
                    isToday = (i == 0)
                )
            )
        }

        val weights = dayStats.mapNotNull { it.recordedWeightKg }
        val startWeight = weights.firstOrNull() ?: (profile?.currentWeightKg ?: 73.5f)
        val endWeight = weights.lastOrNull() ?: (profile?.currentWeightKg ?: 73.5f)
        val avgWeight = if (weights.isNotEmpty()) weights.average().toFloat() else endWeight
        val delta = endWeight - startWeight

        val totalWater = dayStats.sumOf { it.waterIntakeMl }
        val avgWater = totalWater / 7
        val goalMetDays = dayStats.count { it.isWaterGoalMet }

        val totalCalBurned = dayStats.sumOf { it.caloriesBurned }
        val avgCalBurned = totalCalBurned / 7
        val peakDay = dayStats.maxByOrNull { it.caloriesBurned } ?: dayStats.last()
        val totalWorkouts = dayStats.sumOf { it.workoutCount }

        val adherence = (((goalMetDays.toFloat() / 7f) * 50) + ((dayStats.count { it.caloriesBurned >= 300 }.toFloat() / 7f) * 50)).toInt().coerceIn(65, 100)

        val deltaStr = "%.1f".format(delta)
        val paceAr = if (delta > 0.05f) "اكتساب كتلة عضلية بنسق ممتاز (+$deltaStr كجم)" else if (delta < -0.05f) "تنشيف وحرق دهون تدريجي ($deltaStr كجم)" else "ثبات وزني متوازن واستقرار مثالي"
        val paceEn = if (delta > 0.05f) "Steady muscle gain pace (+$deltaStr kg)" else if (delta < -0.05f) "Steady fat loss pace ($deltaStr kg)" else "Balanced weight stability"
        val paceId = if (delta > 0.05f) "Peningkatan massa otot bertahap (+$deltaStr kg)" else if (delta < -0.05f) "Pembakaran lemak bertahap ($deltaStr kg)" else "Stabilitas berat badan seimbang"

        return WeeklyProgressSummary(
            dayStats = dayStats,
            averageWeightKg = (avgWeight * 10).toInt() / 10f,
            startWeightKg = startWeight,
            endWeightKg = endWeight,
            weightDeltaKg = (delta * 10).toInt() / 10f,
            targetWeightKg = profile?.targetWeightKg ?: 78.0f,
            totalWaterMl = totalWater,
            averageDailyWaterMl = avgWater,
            waterGoalMetDays = goalMetDays,
            dailyWaterGoalMl = dailyWaterGoal,
            totalCaloriesBurned = totalCalBurned,
            averageDailyCaloriesBurned = avgCalBurned,
            peakBurnCalories = peakDay.caloriesBurned,
            peakBurnDayNameAr = peakDay.dayNameShortAr,
            peakBurnDayNameEn = peakDay.dayNameShortEn,
            peakBurnDayNameId = peakDay.dayNameShortId,
            totalWorkouts = totalWorkouts,
            adherenceScorePercent = adherence,
            weightPaceStatusAr = paceAr,
            weightPaceStatusEn = paceEn,
            weightPaceStatusId = paceId
        )
    }
}

