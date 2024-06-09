package hr.fer.fercropmanager.alarms.persistence

import hr.fer.fercropmanager.alarms.service.AlarmsState
import kotlinx.coroutines.flow.Flow

interface AlarmsPersistence {

    fun getCachedState(): Flow<AlarmsState>
    suspend fun updateAlarmsState(alarmsState: AlarmsState)
}