package hr.fer.fercropmanager.device.service

import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.device.api.DeviceApi
import hr.fer.fercropmanager.device.api.DeviceDataDto
import hr.fer.fercropmanager.device.api.DeviceValues
import hr.fer.fercropmanager.device.api.toDeviceValues
import hr.fer.fercropmanager.device.persistence.DevicePersistence
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

class DeviceServiceImpl(
    private val deviceApi: DeviceApi,
    private val deviceWebSocket: DeviceWebSocket,
    private val authService: AuthService,
    private val snackbarService: SnackbarService,
    private val devicePersistence: DevicePersistence,
) : DeviceService {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)

    private var devicesList = listOf<Device>()
    private val isShortcutLoadingFlow = MutableStateFlow(false)

    init {
        scope.launch { initialiseSocketConnection() }
    }

    override fun getDeviceState() = combine(
        devicePersistence.getCachedState(),
        isShortcutLoadingFlow,
    ) { deviceState, isShortcutLoading ->
        deviceState.withUpdatedShortcutLoadingState(isShortcutLoading)
    }

    override suspend fun refreshDeviceState() {
        initialiseSocketConnection()
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

    private suspend fun initialiseSocketConnection() {
        devicePersistence.updateDeviceState(DeviceState.Loading)
        val deviceIdNameMap = mutableMapOf<String, String>()
        val token = (authService.getAuthState().first() as AuthState.Success).token
        val customerId = (authService.getAuthState().first() as AuthState.Success).customerId
        deviceApi.getDevices(token, customerId).fold(
            onSuccess = { deviceDto ->
                deviceDto.data.forEach { device -> deviceIdNameMap[device.id.id] = device.name }
            },
            onFailure = { devicePersistence.updateDeviceState(DeviceState.Error) },
        )
        if (deviceIdNameMap.isNotEmpty()) {
            deviceWebSocket.connectWebSocket(
                token = token,
                entityIdList = deviceIdNameMap.keys.toList(),
                onDataReceived = { scope.launch { handleReceivedData(it, deviceIdNameMap) } },
            )
        } else {
            devicePersistence.updateDeviceState(DeviceState.Loaded.Empty)
        }
    }

    private suspend fun handleReceivedData(data: String?, deviceIdNameMap: MutableMap<String, String>) {
        if (data == null) return
        try {
            val deviceDataDto = Json.decodeFromString<DeviceDataDto>(data)
            val entityId = deviceWebSocket.retrieveDeviceId(deviceDataDto.subscriptionId)
            val deviceName = deviceIdNameMap[entityId]
            // TODO Update devices and device data separately
            deviceName?.let { updateDevicesList(deviceDataDto.toDeviceValues(), entityId, it) }
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
    }

    private suspend fun updateDevicesList(deviceValues: DeviceValues, entityId: String, deviceName: String) {
        val currentDevice = Device(
            id = entityId,
            name = deviceName,
            humidity = deviceValues.humidity,
            temperature = deviceValues.temperature,
            moisture = deviceValues.moisture,
            isWateringInProgress = deviceValues.isWateringInProgress,
        )
        val updatedList = devicesList.toMutableList().apply {
            val index = devicesList.indexOfFirst { device -> device.name == deviceName }
            if (index >= 0) set(index, currentDevice) else add(currentDevice)
        }.sortedBy { it.name }
        devicesList = updatedList
        devicePersistence.updateDeviceState(DeviceState.Loaded.Available(updatedList))
    }

    private fun DeviceState.withUpdatedShortcutLoadingState(isLoading: Boolean) = when (this) {
        is DeviceState.Loaded.Available -> this.copy(isShortcutLoading = isLoading)
        else -> this
    }
}
