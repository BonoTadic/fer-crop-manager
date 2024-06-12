package hr.fer.fercropmanager.crop.usecase

import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.device.service.DeviceService
import hr.fer.fercropmanager.device.service.DeviceState
import hr.fer.fercropmanager.device.service.RpcStatus
import hr.fer.fercropmanager.snackbar.service.SnackbarService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val SPRINKLER_DURATION = 10_000L

private const val MOISTURE_SENSOR_TYPE = "moisture_sensor"
private const val TEMP_HUMIDITY_SENSOR_TYPE = "temp_humidity"

class CropUseCaseImpl(
    private val authService: AuthService,
    private val deviceService: DeviceService,
    private val snackbarService: SnackbarService,
) : CropUseCase {

    private val coroutineContext = Dispatchers.Main + SupervisorJob()
    private val scope = CoroutineScope(coroutineContext)

    // TODO Add logic to calculate what crop should receive these values
    private val isShortcutLoadingFlow = MutableStateFlow(false)
    private val isWateringInProgressFlow = MutableStateFlow(false)

    private val cropsFlow = combine(
        deviceService.getDeviceState().filterIsInstance<DeviceState.Loaded.Available>(),
        deviceService.getDeviceValues(),
        isWateringInProgressFlow,
    ) { deviceState, deviceValues, isWateringInProgress ->
        deviceState.devices
            .filter { it.type == MOISTURE_SENSOR_TYPE || it.type == TEMP_HUMIDITY_SENSOR_TYPE }
            .map { device -> device.toCrop(deviceValues[device.id], isWateringInProgress) }
    }

    override fun getCropStateFlow() = combine(
        authService.getAuthState().filterIsInstance<AuthState.Success>().map { authState -> authState.toUserData() },
        deviceService.getDeviceState(),
        cropsFlow,
        isShortcutLoadingFlow,
    ) { userData, deviceState, crops, isShortcutLoading ->
        when (deviceState) {
            DeviceState.Initial, DeviceState.Loading -> CropState.Loaded.Loading(userData)
            DeviceState.Error -> CropState.Error(userData)
            DeviceState.Loaded.Empty -> CropState.Loaded.Empty(userData)
            is DeviceState.Loaded.Available -> CropState.Loaded.Available(
                userData = userData,
                isShortcutLoading = isShortcutLoading,
                crops = crops,
            )
        }
    }

    override suspend fun activateSprinklers(targetValue: String) {
        deviceService.activateSprinkler(
            targetValue = targetValue,
            onStatusChange = { status ->
                scope.launch { handleSprinklerRpcResponse(status) }
            }
        )
    }

    override suspend fun refreshCrops() {
        deviceService.refreshDevices()
    }

    private suspend fun handleSprinklerRpcResponse(rpcStatus: RpcStatus) {
        when (rpcStatus) {
            RpcStatus.Loading -> {
                isShortcutLoadingFlow.value = true
            }
            RpcStatus.Error -> {
                isShortcutLoadingFlow.value = false
                snackbarService.notifyUser(message = "Watering process failed. Please try again.")
            }
            RpcStatus.Success -> {
                isShortcutLoadingFlow.value = false
                isWateringInProgressFlow.value = true
                snackbarService.notifyUser(message = "Watering started successfully!")
                delay(SPRINKLER_DURATION)
                isWateringInProgressFlow.value = false
            }
        }
    }
}
