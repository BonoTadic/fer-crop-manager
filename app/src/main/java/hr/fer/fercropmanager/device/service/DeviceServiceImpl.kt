package hr.fer.fercropmanager.device.service

import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.device.api.DeviceApi
import hr.fer.fercropmanager.device.api.DeviceDataDto
import hr.fer.fercropmanager.device.persistence.DevicePersistence
import hr.fer.fercropmanager.device.persistence.DeviceValuesPersistence
import hr.fer.fercropmanager.device.websocket.DeviceWebSocket
import hr.fer.fercropmanager.snackbar.service.SnackbarService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val DEFAULT_TYPE = "default"

class DeviceServiceImpl(
    private val deviceApi: DeviceApi,
    private val deviceWebSocket: DeviceWebSocket,
    private val authService: AuthService,
    private val snackbarService: SnackbarService,
    private val devicePersistence: DevicePersistence,
    private val deviceValuesPersistence: DeviceValuesPersistence,
) : DeviceService {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)

    private val deviceIdNameMap = mutableMapOf<String, String>()
    private val isShortcutLoadingFlow = MutableStateFlow(false)

    private var deviceValuesMap = mapOf<String, DeviceValues>()

    init {
        scope.launch { fetchDevices() }
    }

    override fun getDeviceState() = combine(
        devicePersistence.getCachedDeviceState(),
        isShortcutLoadingFlow,
    ) { deviceState, isShortcutLoading ->
        deviceState.withUpdatedShortcutLoadingState(isShortcutLoading)
    }

    override fun getDeviceValues() = deviceValuesPersistence.getCachedDeviceValues()

    override suspend fun refreshDevices() {
        fetchDevices()
    }

    override suspend fun startActuation(targetValue: String) {
        isShortcutLoadingFlow.value = true
        val token = (authService.getAuthState().first() as AuthState.Success).token
        val entityId = devicePersistence.getSelectedDeviceId().first()
        deviceApi.startActuation(token, entityId, targetValue).fold(
            onSuccess = {
                isShortcutLoadingFlow.value = false
                snackbarService.notifyUser(message = "Watering started successfully!")
            },
            onFailure = {
                isShortcutLoadingFlow.value = false
                snackbarService.notifyUser(message = "Watering process failed. Please try again.")
            },
        )
    }

    private suspend fun fetchDevices() {
        devicePersistence.updateDeviceState(DeviceState.Loading)
        val token = (authService.getAuthState().first() as AuthState.Success).token
        val customerId = (authService.getAuthState().first() as AuthState.Success).customerId
        deviceApi.getDevices(token, customerId).fold(
            onSuccess = { deviceDto ->
                val devices = deviceDto.data.map { it.toDevice() }
                if (devices.isNotEmpty()) {
                    val sensors = devices.filter { it.type != DEFAULT_TYPE }
                    sensors.forEach { sensor -> deviceIdNameMap[sensor.id] = sensor.name }
                    devicePersistence.updateDeviceState(DeviceState.Loaded.Available(devices))
                    initialiseSocketConnection(token)
                } else {
                    devicePersistence.updateDeviceState(DeviceState.Loaded.Empty)
                }
            },
            onFailure = { devicePersistence.updateDeviceState(DeviceState.Error) },
        )
    }

    private suspend fun initialiseSocketConnection(token: String) {
        deviceWebSocket.connectWebSocket(
            token = token,
            entityIdList = deviceIdNameMap.keys.toList(),
            onDataReceived = { scope.launch { handleReceivedData(it) } },
        )
    }

    private suspend fun handleReceivedData(data: String?) {
        if (data == null) return
        try {
            val deviceDataDto = Json.decodeFromString<DeviceDataDto>(data)
            val entityId = deviceWebSocket.retrieveDeviceId(deviceDataDto.subscriptionId)
            val updatedDeviceValuesMap = deviceValuesMap.toMutableMap().apply {
                put(entityId, deviceDataDto.toDeviceValues())
            }.toMutableMap()
            deviceValuesMap = updatedDeviceValuesMap
            deviceValuesPersistence.updateDeviceValues(deviceValuesMap)
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
    }

    private fun DeviceState.withUpdatedShortcutLoadingState(isLoading: Boolean) = when (this) {
        is DeviceState.Loaded.Available -> this.copy(isShortcutLoading = isLoading)
        else -> this
    }
}
