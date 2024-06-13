package hr.fer.fercropmanager.alarms.service

import hr.fer.fercropmanager.alarms.api.AlarmsApi
import hr.fer.fercropmanager.alarms.api.AlarmsDto
import hr.fer.fercropmanager.alarms.persistence.AlarmsPersistence
import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.snackbar.service.SnackbarService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

private const val POLLING_DELAY = 10_000L

class AlarmsServiceImpl(
    private val alarmsApi: AlarmsApi,
    private val authService: AuthService,
    private val alarmsPersistence: AlarmsPersistence,
    private val snackbarService: SnackbarService,
) : AlarmsService {

    private val isButtonLoadingFlow = MutableStateFlow(false)

    override fun getAlarmsListState() = alarmsPersistence.getCachedAlarmsListState()

    override fun getAlarmState() = combine(
        alarmsPersistence.getCachedAlarmState(),
        isButtonLoadingFlow,
    ) { alarmState, isButtonLoading ->
        when (alarmState) {
            AlarmState.Error, AlarmState.Loading -> alarmState
            is AlarmState.Loaded -> alarmState.withUpdatedLoadingState(isButtonLoading)
        }
    }

    override suspend fun getAlarm(alarmId: String) {
        refreshAlarm(alarmId = alarmId, showLoading = true)
    }

    override suspend fun acknowledgeAlarm(alarmId: String) {
        val token = (authService.getAuthState().first() as AuthState.Success).token
        alarmsApi.acknowledgeAlarm(token, alarmId).fold(
            onSuccess = { fetchAlarms() },
            onFailure = {
                // TODO
            },
        )
    }

    override suspend fun clearAlarm(alarmId: String) {
        isButtonLoadingFlow.value = true
        val token = (authService.getAuthState().first() as AuthState.Success).token
        alarmsApi.clearAlarm(token, alarmId).fold(
            onSuccess = {
                isButtonLoadingFlow.value = false
                refreshAlarm(alarmId = alarmId, showLoading = false)
                fetchAlarms()
            },
            onFailure = {
                isButtonLoadingFlow.value = false
                snackbarService.notifyUser("Failed to clear alarm. Please try again.")
            },
        )
    }

    override suspend fun startPollingAlarms() {
        while (true) {
            fetchAlarms()
            delay(POLLING_DELAY)
        }
    }

    private suspend fun refreshAlarm(alarmId: String, showLoading: Boolean) {
        if (showLoading) alarmsPersistence.updateAlarmState(AlarmState.Loading)
        val token = (authService.getAuthState().first() as AuthState.Success).token
        alarmsApi.getAlarm(token, alarmId).fold(
            onSuccess = { alarmsPersistence.updateAlarmState(AlarmState.Loaded(it.toAlarm())) },
            onFailure = { alarmsPersistence.updateAlarmState(AlarmState.Error) },
        )
    }

    private suspend fun fetchAlarms() {
        val token = (authService.getAuthState().first() as AuthState.Success).token
        alarmsApi.getAllAlarms(token).fold(
            onSuccess = { alarmsDto -> updateAlarmsList(alarmsDto) },
            onFailure = {
                // TODO
            },
        )
    }

    private suspend fun updateAlarmsList(alarmsDto: AlarmsDto) {
        if (alarmsDto.data.isEmpty()) {
            alarmsPersistence.updateAlarmsListState(AlarmsListState.Empty)
        } else {
            alarmsPersistence.updateAlarmsListState(AlarmsListState.Available(alarmsDto.data.map { it.toAlarm() }))
        }
    }

    private fun AlarmState.Loaded.withUpdatedLoadingState(isLoading: Boolean) =
        copy(isButtonLoading = isLoading)
}
