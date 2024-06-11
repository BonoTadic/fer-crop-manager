package hr.fer.fercropmanager.alarms.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fer.fercropmanager.alarms.service.AlarmsService
import hr.fer.fercropmanager.alarms.service.AlarmsListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmsListViewModel(private val alarmsService: AlarmsService) : ViewModel() {

    val state = alarmsService.getAlarmsListState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AlarmsListState.Loading,
        )

    fun onInteraction(interaction: AlarmsListInteraction) {
        when (interaction) {
            is AlarmsListInteraction.AlarmClick -> viewModelScope.launch {
                alarmsService.acknowledgeAlarm(interaction.alarmId)
            }
        }
    }
}