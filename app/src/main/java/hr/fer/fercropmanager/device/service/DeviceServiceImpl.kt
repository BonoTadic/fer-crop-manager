package hr.fer.fercropmanager.device.service

import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.device.api.DeviceApi
import hr.fer.fercropmanager.device.api.DeviceDataDto
import hr.fer.fercropmanager.device.persistence.DevicePersistence
import hr.fer.fercropmanager.device.persistence.DeviceValuesPersistence
import hr.fer.fercropmanager.device.websocket.DeviceWebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val MOISTURE_SENSOR_TYPE = "moisture_sensor"
private const val TEMP_HUMIDITY_SENSOR_TYPE = "temp_humidity"
private const val SPRAYER_DEVICE_TYPE = "sprayer"
private const val LED_DEVICE_TYPE = "led_light"

class DeviceServiceImpl(
    private val deviceApi: DeviceApi,
    private val deviceWebSocket: DeviceWebSocket,
    private val authService: AuthService,
    private val devicePersistence: DevicePersistence,
    private val deviceValuesPersistence: DeviceValuesPersistence,
) : DeviceService {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)

    private val deviceIdNameMap = mutableMapOf<String, String>()

    private var deviceValuesMap = mapOf<String, DeviceValues>()

    init {
        scope.launch { fetchDevices() }
    }

    override fun getDeviceState() = devicePersistence.getCachedDeviceState()

    override fun getDeviceValues() = deviceValuesPersistence.getCachedDeviceValues()

    override suspend fun refreshDevices() {
        fetchDevices()
    }

    override suspend fun activateSprinkler(onStatusChange: (RpcStatus) -> Unit) {
        onStatusChange(RpcStatus.Loading)
        val token = (authService.getAuthState().first() as AuthState.Success).token
        val devices = (devicePersistence.getCachedDeviceState().first() as DeviceState.Loaded.Available).devices
        val spraySensor = devices.first { it.type == SPRAYER_DEVICE_TYPE }
        deviceApi.activateSprinkler(token, spraySensor.id).fold(
            onSuccess = { onStatusChange(RpcStatus.Success) },
            onFailure = { onStatusChange(RpcStatus.Error) },
        )
    }

    override suspend fun setLedStatus(targetValue: Int, onStatusChange: (RpcStatus) -> Unit) {
        onStatusChange(RpcStatus.Loading)
        val token = (authService.getAuthState().first() as AuthState.Success).token
        val devices = (devicePersistence.getCachedDeviceState().first() as DeviceState.Loaded.Available).devices
        val ledDevice = devices.first { it.type == LED_DEVICE_TYPE }
        deviceApi.setLedStatus(token, ledDevice.id, targetValue).fold(
            onSuccess = { onStatusChange(RpcStatus.Success) },
            onFailure = { onStatusChange(RpcStatus.Error) },
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
                    val sensors = devices.filter {
                        it.type == MOISTURE_SENSOR_TYPE || it.type == TEMP_HUMIDITY_SENSOR_TYPE
                    }
                    sensors.forEach { sensor -> deviceIdNameMap[sensor.id] = sensor.name }
                    devices.forEach {
                        println("bono device: $it")
                    }
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
}
