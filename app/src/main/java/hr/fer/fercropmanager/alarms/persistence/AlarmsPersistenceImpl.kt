package hr.fer.fercropmanager.alarms.persistence

import hr.fer.fercropmanager.alarms.service.AlarmState
import hr.fer.fercropmanager.alarms.service.AlarmsListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlarmsPersistenceImpl : AlarmsPersistence {

    private val cachedAlarmsListState: MutableStateFlow<AlarmsListState> = MutableStateFlow(AlarmsListState.Loading)
    private val cachedAlarmState: MutableStateFlow<AlarmState> = MutableStateFlow(AlarmState.Loading)

    override fun getCachedAlarmsListState() = cachedAlarmsListState.asStateFlow()

    override fun getCachedAlarmState() = cachedAlarmState.asStateFlow()

    override suspend fun updateAlarmsListState(alarmsListState: AlarmsListState) {
        cachedAlarmsListState.value = alarmsListState
    }

    override suspend fun updateAlarmState(alarmState: AlarmState) {
        cachedAlarmState.value = alarmState
    }
}
