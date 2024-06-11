package hr.fer.fercropmanager.alarms.persistence

import hr.fer.fercropmanager.alarms.service.AlarmState
import hr.fer.fercropmanager.alarms.service.AlarmsListState
import kotlinx.coroutines.flow.Flow

interface AlarmsPersistence {

    fun getCachedAlarmsListState(): Flow<AlarmsListState>
    fun getCachedAlarmState(): Flow<AlarmState>
    suspend fun updateAlarmsListState(alarmsListState: AlarmsListState)
    suspend fun updateAlarmState(alarmState: AlarmState)
}