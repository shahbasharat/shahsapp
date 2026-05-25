package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AcademyViewModel(private val repository: AcademyRepository) : ViewModel() {

    // All progress list from Room
    val allProgress: StateFlow<List<LessonProgress>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Stats from Room
    val globalStats: StateFlow<GlobalStats?> = repository.globalStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All available lessons
    val lessons: List<LessonModel> = LessonContent.LESSONS

    // Navigation and detail state
    private val _selectedLessonId = MutableStateFlow<Int?>(null)
    val selectedLessonId: StateFlow<Int?> = _selectedLessonId.asStateFlow()

    val selectedLesson: StateFlow<LessonModel?> = _selectedLessonId
        .map { id -> id?.let { lessons.find { l -> l.id == it } } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Command Search and Filters state
    private val _commandSearchQuery = MutableStateFlow("")
    val commandSearchQuery: StateFlow<String> = _commandSearchQuery.asStateFlow()

    private val _commandFilter = MutableStateFlow("All")
    val commandFilter: StateFlow<String> = _commandFilter.asStateFlow()

    // Persistent in-memory lab step checking mapped by lessonId -> List<Boolean>
    private val _checkedLabSteps = MutableStateFlow<Map<Int, List<Boolean>>>(emptyMap())
    val checkedLabSteps: StateFlow<Map<Int, List<Boolean>>> = _checkedLabSteps.asStateFlow()

    // Active Quiz states for selected lesson
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswerOption = MutableStateFlow<String?>(null)
    val selectedAnswerOption: StateFlow<String?> = _selectedAnswerOption.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    private val _quizStepCorrectAnswers = MutableStateFlow(0)
    val quizStepCorrectAnswers: StateFlow<Int> = _quizStepCorrectAnswers.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    private val _lastQuizScore = MutableStateFlow<Int?>(null)
    val lastQuizScore: StateFlow<Int?> = _lastQuizScore.asStateFlow()

    private val _wasLastQuizPassed = MutableStateFlow<Boolean?>(null)
    val wasLastQuizPassed: StateFlow<Boolean?> = _wasLastQuizPassed.asStateFlow()

    // Confetti flow for animations
    private val _confettiTrigger = MutableSharedFlow<Unit>(replay = 0)
    val confettiTrigger: SharedFlow<Unit> = _confettiTrigger.asSharedFlow()

    init {
        // Prepare initial stats if empty
        viewModelScope.launch {
            repository.touchLesson(1)
        }
    }

    fun selectLesson(lessonId: Int) {
        _selectedLessonId.value = lessonId
        resetQuiz()
        viewModelScope.launch {
            repository.touchLesson(lessonId)
        }
    }

    fun deselectLesson() {
        _selectedLessonId.value = null
    }

    fun setCommandSearch(query: String) {
        _commandSearchQuery.value = query
    }

    fun setCommandFilter(filter: String) {
        _commandFilter.value = filter
    }

    fun markTheoryRead(lessonId: Int) {
        viewModelScope.launch {
            repository.markTheoryRead(lessonId)
        }
    }

    fun toggleLabStep(lessonId: Int, stepIndex: Int, checked: Boolean) {
        val currentMap = _checkedLabSteps.value
        val lesson = lessons.find { it.id == lessonId } ?: return
        val currentList = currentMap[lessonId] ?: List(lesson.labSteps.size) { false }
        val updatedList = currentList.toMutableList().apply {
            if (stepIndex in indices) {
                set(stepIndex, checked)
            }
        }
        
        _checkedLabSteps.value = currentMap + (lessonId to updatedList)

        // Check if all steps completed
        val allCompleted = updatedList.all { it }
        viewModelScope.launch {
            repository.markLabCompleted(lessonId, allCompleted)
            if (allCompleted) {
                // Trigger celebratory sound/animation or state change if wanted
            }
        }
    }

    fun resetLab(lessonId: Int) {
        val currentMap = _checkedLabSteps.value
        val lesson = lessons.find { it.id == lessonId } ?: return
        val cleared = List(lesson.labSteps.size) { false }
        
        _checkedLabSteps.value = currentMap + (lessonId to cleared)
        viewModelScope.launch {
            repository.markLabCompleted(lessonId, false)
        }
    }

    // Quiz flows
    fun selectQuizAnswer(option: String) {
        if (_isAnswerSubmitted.value) return // Prevent editing after submission
        _selectedAnswerOption.value = option
    }

    fun submitQuizAnswer() {
        val activeLesson = lessons.find { it.id == _selectedLessonId.value } ?: return
        val currentQuestionIndex = _currentQuestionIndex.value
        if (currentQuestionIndex >= activeLesson.quiz.size) return
        
        val questionModel = activeLesson.quiz[currentQuestionIndex]
        val selectedOption = _selectedAnswerOption.value ?: return

        _isAnswerSubmitted.value = true
        val isCorrect = selectedOption == questionModel.answer
        _isAnswerCorrect.value = isCorrect

        if (isCorrect) {
            _quizStepCorrectAnswers.value += 1
        }
    }

    fun nextQuizQuestion() {
        val activeLesson = lessons.find { it.id == _selectedLessonId.value } ?: return
        val currentIndex = _currentQuestionIndex.value
        
        if (currentIndex < activeLesson.quiz.size - 1) {
            _currentQuestionIndex.value = currentIndex + 1
            _selectedAnswerOption.value = null
            _isAnswerSubmitted.value = false
            _isAnswerCorrect.value = null
        } else {
            // End of Quiz
            val score = _quizStepCorrectAnswers.value
            val total = activeLesson.quiz.size
            val passed = score >= 3 // Pass at 3/5 (60%) or more
            
            _lastQuizScore.value = score
            _wasLastQuizPassed.value = passed
            _isQuizFinished.value = true

            if (passed) {
                viewModelScope.launch {
                    _confettiTrigger.emit(Unit)
                }
            }

            viewModelScope.launch {
                repository.saveQuizScore(activeLesson.id, score, passed)
            }
        }
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _selectedAnswerOption.value = null
        _isAnswerSubmitted.value = false
        _isAnswerCorrect.value = null
        _quizStepCorrectAnswers.value = 0
        _isQuizFinished.value = false
        _lastQuizScore.value = null
        _wasLastQuizPassed.value = null
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            repository.touchLesson(1)
            resetQuiz()
        }
    }
}
