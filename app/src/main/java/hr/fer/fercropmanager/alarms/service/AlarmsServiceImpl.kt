package hr.fer.fercropmanager.alarms.service

import hr.fer.fercropmanager.alarms.api.AlarmsApi
import hr.fer.fercropmanager.alarms.api.AlarmsDto
import hr.fer.fercropmanager.alarms.persistence.AlarmsPersistence
import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.device.service.Device
import hr.fer.fercropmanager.device.service.DeviceService
import hr.fer.fercropmanager.device.service.DeviceState
import hr.fer.fercropmanager.network.DEVICE_ENTITY_TYPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val POLLING_DELAY = 3_000L

class AlarmsServiceImpl(
    private val alarmsApi: AlarmsApi,
    private val authService: AuthService,
    private val deviceService: DeviceService,
    private val alarmsPersistence: AlarmsPersistence,
) : AlarmsService {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)

    private var alarmsList = listOf<Alarm>()
    private val alarmsIdSet = mutableSetOf<String>()

    init {
        scope.launch { startPollingAlarms() }
    }

    override fun getAlarmsState() = alarmsPersistence.getCachedState()

    private suspend fun startPollingAlarms() {
        while (true) {
            deviceService.getDeviceState().collectLatest { deviceState ->
                when (deviceState) {
                    is DeviceState.Loaded.Available -> fetchAlarms(deviceState.devices)
                    DeviceState.Error,
                    DeviceState.Initial,
                    DeviceState.Loaded.Empty,
                    DeviceState.Loading -> Unit
                }
            }
            delay(POLLING_DELAY)
        }
    }

    private suspend fun fetchAlarms(devices: List<Device>) {
        if (devices.isNotEmpty()) {
            val entityIdList = devices.map { it.id }
            val token = (authService.getAuthState().first() as AuthState.Success).token
            entityIdList.forEach { entityId ->
                alarmsApi.getAlarms(token, DEVICE_ENTITY_TYPE, entityId).fold(
                    onSuccess = { alarmsDto -> updateAlarmsList(alarmsDto, devices) },
                    onFailure = {
                        // TODO Show a snackbar
                    },
                )
            }
        }
    }

    private suspend fun updateAlarmsList(alarmsDto: AlarmsDto, devices: List<Device>) {
        val uniqueNewAlarmsData = alarmsDto.data.filter { alarm -> alarm.id.id !in alarmsIdSet }
        if (uniqueNewAlarmsData.isNotEmpty()) {
            alarmsList = alarmsList + uniqueNewAlarmsData.map { data -> data.toAlarm(devices) }
            alarmsPersistence.updateAlarmsState(AlarmsState.Available(alarmsList))
        }
    }
}