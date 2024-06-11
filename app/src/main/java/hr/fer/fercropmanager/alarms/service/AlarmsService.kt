package hr.fer.fercropmanager.alarms.service

import kotlinx.coroutines.flow.Flow

interface AlarmsService {

    fun getAlarmsListState(): Flow<AlarmsListState>
    fun getAlarmState(): Flow<AlarmState>
    suspend fun getAlarm(alarmId: String)
    suspend fun acknowledgeAlarm(alarmId: String)
    suspend fun clearAlarm(alarmId: String)
}