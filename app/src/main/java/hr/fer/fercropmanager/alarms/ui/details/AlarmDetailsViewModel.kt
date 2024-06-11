package hr.fer.fercropmanager.alarms.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fer.fercropmanager.alarms.service.AlarmState
import hr.fer.fercropmanager.alarms.service.AlarmsService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmDetailsViewModel(private val alarmId: String, private val alarmsService: AlarmsService) : ViewModel() {

    init {
        viewModelScope.launch { alarmsService.getAlarm(alarmId) }
    }

    val state = alarmsService.getAlarmState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AlarmState.Loading,
        )

    fun onInteraction(interaction: AlarmDetailsInteraction) {
        when (interaction) {
            is AlarmDetailsInteraction.ClearAlarm -> viewModelScope.launch {
                alarmsService.clearAlarm(interaction.alarmId)
            }
            AlarmDetailsInteraction.RetryClick -> viewModelScope.launch {
                alarmsService.getAlarm(alarmId)
            }
        }
    }
}
