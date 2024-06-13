package hr.fer.fercropmanager.crop.usecase

import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.crop.ui.plants.service.PlantsService
import hr.fer.fercropmanager.crop.ui.utils.combine
import hr.fer.fercropmanager.device.service.DeviceService
import hr.fer.fercropmanager.device.service.DeviceState
import hr.fer.fercropmanager.device.service.RpcStatus
import hr.fer.fercropmanager.snackbar.service.SnackbarService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val SPRINKLER_DURATION = 10_000L

private const val MOISTURE_SENSOR_TYPE = "moisture_sensor"
private const val TEMP_HUMIDITY_SENSOR_TYPE = "temp_humidity"

class CropUseCaseImpl(
    private val authService: AuthService,
    private val deviceService: DeviceService,
    private val snackbarService: SnackbarService,
    plantsService: PlantsService,
) : CropUseCase {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)

    private val wateringButtonLoadingMapFlow = MutableStateFlow(mapOf<String, Boolean>())
    private val isWateringInProgressMapFlow = MutableStateFlow(mapOf<String, Boolean>())

    private val ledButtonLoadingMapFlow = MutableStateFlow(mapOf<String, Boolean>())

    private val selectedDeviceIdFlow = deviceService.getSelectedDeviceId()
        .stateIn(scope = scope, started = SharingStarted.Eagerly, initialValue = "")

    private val cropsFlow = combine(
        deviceService.getDeviceState().filterIsInstance<DeviceState.Loaded.Available>(),
        deviceService.getDeviceValues(),
        selectedDeviceIdFlow,
        plantsService.getPlants(),
        wateringButtonLoadingMapFlow,
        isWateringInProgressMapFlow,
    ) { deviceState, deviceValues, selectedDeviceId, plantsMap, isWateringButtonLoadingMap, isWateringInProgressMap ->
        deviceState.devices
            .filter { it.type == MOISTURE_SENSOR_TYPE || it.type == TEMP_HUMIDITY_SENSOR_TYPE }
            .map { device ->
                device.toCrop(
                    deviceValues = deviceValues[device.id],
                    isWateringInProgress = isWateringInProgressMap[selectedDeviceId] ?: false,
                    isWateringButtonLoading = isWateringButtonLoadingMap[selectedDeviceId] ?: false,
                    plantsMap = plantsMap,
                )
            }
    }

    override fun getCropStateFlow() = combine(
        authService.getAuthState().filterIsInstance<AuthState.Success>().map { authState -> authState.toUserData() },
        deviceService.getDeviceState(),
        cropsFlow,
    ) { userData, deviceState, crops ->
        when (deviceState) {
            DeviceState.Initial, DeviceState.Loading -> CropState.Loaded.Loading(userData)
            DeviceState.Error -> CropState.Error(userData)
            DeviceState.Loaded.Empty -> CropState.Loaded.Empty(userData)
            is DeviceState.Loaded.Available -> CropState.Loaded.Available(
                userData = userData,
                isLedButtonLoading = false,
                crops = crops,
            )
        }
    }

    override suspend fun activateSprinkler() {
        deviceService.activateSprinkler(
            onStatusChange = { status -> scope.launch { handleSprinklerRpcResponse(status) } }
        )
    }

    override suspend fun refreshCrops() {
        deviceService.refreshDevices()
    }

    override suspend fun setLedStatus(targetValue: Int) {
        deviceService.setLedStatus(
            targetValue = targetValue,
            onStatusChange = {
                // TODO Handle UI change
            },
        )
    }

    private suspend fun handleSprinklerRpcResponse(rpcStatus: RpcStatus) {
        when (rpcStatus) {
            RpcStatus.Loading -> {
                updateMapValue(wateringButtonLoadingMapFlow, true)
            }
            RpcStatus.Error -> {
                updateMapValue(wateringButtonLoadingMapFlow, false)
                snackbarService.notifyUser(message = "Watering process failed. Please try again.")
            }
            RpcStatus.Success -> {
                updateMapValue(wateringButtonLoadingMapFlow, false)
                updateMapValue(isWateringInProgressMapFlow, true)
                snackbarService.notifyUser(message = "Sprinkler activated!")
                delay(SPRINKLER_DURATION)
                updateMapValue(isWateringInProgressMapFlow, false)
            }
        }
    }

    private fun updateMapValue(mapFlow: MutableStateFlow<Map<String, Boolean>>, value: Boolean) {
        val updatedMapState = mapFlow.value.toMutableMap().apply {
            put(selectedDeviceIdFlow.value, value)
        }
        mapFlow.value = updatedMapState
    }
}
