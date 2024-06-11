package hr.fer.fercropmanager.alarms.ui.details

interface AlarmDetailsInteraction {

    data class ClearAlarm(val alarmId: String) : AlarmDetailsInteraction
    data object RetryClick : AlarmDetailsInteraction
}
