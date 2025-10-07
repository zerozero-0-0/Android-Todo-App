package com.example.todox.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todox.data.repo.SettingsRepository
import com.example.todox.workers.DailyResetScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val dailyResetScheduler: DailyResetScheduler
) : ViewModel() {

    val resetHour: StateFlow<Int> = settingsRepository.resetHour
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsRepository.DEFAULT_RESET_HOUR
        )

    fun updateResetHour(hour: Int) {
        val normalized = hour.coerceIn(0, 23)
        viewModelScope.launch {
            settingsRepository.setResetHour(normalized)
            dailyResetScheduler.ensureScheduled(normalized)
        }
    }
}
