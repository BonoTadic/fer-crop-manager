package hr.fer.fercropmanager.alarms.api

interface AlarmsApi {

    suspend fun getAlarm(token: String, alarmId: String): Result<AlarmDto>
    suspend fun getAlarms(token: String, entityType: String, entityId: String): Result<AlarmsDto>
}