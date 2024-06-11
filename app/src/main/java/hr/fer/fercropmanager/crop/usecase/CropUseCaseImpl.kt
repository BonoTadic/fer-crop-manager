package hr.fer.fercropmanager.crop.usecase

import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthState
import hr.fer.fercropmanager.device.service.DeviceService
import hr.fer.fercropmanager.device.service.DeviceState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

class CropUseCaseImpl(
    private val authService: AuthService,
    private val deviceService: DeviceService,
) : CropUseCase {

    override fun getCropStateFlow() = combine(
        authService.getAuthState().filterIsInstance<AuthState.Success>().map { authState -> authState.toUserData() },
        deviceService.getDeviceState(),
    ) { userData, deviceState ->
        when (deviceState) {
            DeviceState.Initial, DeviceState.Loading -> CropState.Loaded.Loading(userData)
            DeviceState.Error -> CropState.Error(userData)
            DeviceState.Loaded.Empty -> CropState.Loaded.Empty(userData)
            is DeviceState.Loaded.Available -> {
                CropState.Loaded.Available(
                    userData = userData,
                    isShortcutLoading = deviceState.isShortcutLoading,
                    crops = deviceState.devices.map { device -> device.toCrop() },
                )
            }
        }
    }

    override suspend fun startWatering(targetValue: String) {
        deviceService.startActuation(targetValue)
    }

    override suspend fun refreshCrops() {
        deviceService.refreshDeviceState()
    }
}
