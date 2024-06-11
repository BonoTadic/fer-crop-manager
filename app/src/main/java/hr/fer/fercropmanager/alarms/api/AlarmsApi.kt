package hr.fer.fercropmanager.alarms.api

interface AlarmsApi {

    suspend fun getAlarm(token: String, alarmId: String): Result<AlarmDto>
    suspend fun getAllAlarms(token: String): Result<AlarmsDto>
    suspend fun acknowledgeAlarm(token: String, alarmId: String): Result<Unit>
    suspend fun clearAlarm(token: String, alarmId: String): Result<Unit>
}
