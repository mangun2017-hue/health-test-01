package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DailyRecordRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface DailyCheckUiState {
    data object Loading : DailyCheckUiState

    data class Ready(
        val dateKey: String,
        val formattedDate: String,
        val dayOfWeek: String,
        val morningTaken: Boolean,
        val lunchTaken: Boolean,
        val dinnerTaken: Boolean,
        val waterCups: Int,
    ) : DailyCheckUiState {
        val takenMedicationCount: Int
            get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }
    }
}

class DailyCheckViewModel(
    private val repository: DailyRecordRepository,
    private val currentDateProvider: () -> Date = { Date() },
    enableMidnightTicker: Boolean = true,
) : ViewModel() {

    private val currentDate = MutableStateFlow(currentDateProvider())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DailyCheckUiState> = currentDate
        .map { date -> formatDateInfo(date) }
        .flatMapLatest { dateInfo ->
            repository.observeRecord(dateInfo.dateKey).map { record ->
                DailyCheckUiState.Ready(
                    dateKey = dateInfo.dateKey,
                    formattedDate = dateInfo.formattedDate,
                    dayOfWeek = dateInfo.dayOfWeek,
                    morningTaken = record.morningTaken,
                    lunchTaken = record.lunchTaken,
                    dinnerTaken = record.dinnerTaken,
                    waterCups = record.waterCups,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyCheckUiState.Loading,
        )

    init {
        val initialKey = formatKey(currentDate.value)
        viewModelScope.launch {
            repository.clearStaleRecordsIfNeeded(initialKey)
        }
        if (enableMidnightTicker) {
            scheduleMidnightReset()
        }
    }

    private fun scheduleMidnightReset() {
        viewModelScope.launch {
            while (isActive) {
                val now = currentDateProvider()
                val millisUntilMidnight = calculateMillisUntilNextMidnight(now)
                delay(millisUntilMidnight)
                refreshDateIfNeeded()
            }
        }
    }

    fun refreshDateIfNeeded(): Boolean {
        val latest = currentDateProvider()
        val latestKey = formatKey(latest)
        val currentKey = formatKey(currentDate.value)
        if (latestKey != currentKey) {
            currentDate.value = latest
            viewModelScope.launch {
                repository.clearStaleRecordsIfNeeded(latestKey)
            }
            return true
        }
        return false
    }

    fun toggleMorning() {
        refreshDateIfNeeded()
        val key = formatKey(currentDate.value)
        viewModelScope.launch {
            repository.toggleMorning(key)
        }
    }

    fun toggleLunch() {
        refreshDateIfNeeded()
        val key = formatKey(currentDate.value)
        viewModelScope.launch {
            repository.toggleLunch(key)
        }
    }

    fun toggleDinner() {
        refreshDateIfNeeded()
        val key = formatKey(currentDate.value)
        viewModelScope.launch {
            repository.toggleDinner(key)
        }
    }

    fun addWaterCup() {
        refreshDateIfNeeded()
        val key = formatKey(currentDate.value)
        viewModelScope.launch {
            repository.incrementWater(key)
        }
    }

    fun removeWaterCup() {
        refreshDateIfNeeded()
        val key = formatKey(currentDate.value)
        viewModelScope.launch {
            repository.decrementWater(key)
        }
    }

    private fun formatKey(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    private fun formatDateInfo(date: Date): DateInfo {
        return DateInfo(
            dateKey = formatKey(date),
            formattedDate = SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN).format(date),
            dayOfWeek = SimpleDateFormat("EEEE", Locale.KOREAN).format(date),
        )
    }

    private data class DateInfo(
        val dateKey: String,
        val formattedDate: String,
        val dayOfWeek: String,
    )

    companion object {
        internal fun calculateMillisUntilNextMidnight(now: Date): Long {
            val nextMidnight = Calendar.getInstance().apply {
                time = now
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 50)
            }
            return (nextMidnight.timeInMillis - now.time).coerceAtLeast(100L)
        }
    }

    class Factory(
        private val repository: DailyRecordRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DailyCheckViewModel(repository) as T
        }
    }
}
