package hr.fer.fercropmanager.alarms.ui.list

sealed interface AlarmsListInteraction {

    data class AlarmClick(val alarmId: String) : AlarmsListInteraction
}
