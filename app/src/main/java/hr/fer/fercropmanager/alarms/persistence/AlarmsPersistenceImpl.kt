package hr.fer.fercropmanager.alarms.persistence

import hr.fer.fercropmanager.alarms.service.AlarmsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlarmsPersistenceImpl : AlarmsPersistence {

    private val cachedState: MutableStateFlow<AlarmsState> = MutableStateFlow(AlarmsState.Empty)

    override fun getCachedState() = cachedState.asStateFlow()

    override suspend fun updateAlarmsState(alarmsState: AlarmsState) {
        cachedState.value = alarmsState
    }
}