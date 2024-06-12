package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DevicePersistenceImpl : DevicePersistence {

    private val cachedDeviceState: MutableStateFlow<DeviceState> = MutableStateFlow(DeviceState.Initial)

    override fun getCachedDeviceState() = cachedDeviceState.asStateFlow()

    override suspend fun updateDeviceState(deviceState: DeviceState) {
        cachedDeviceState.value = deviceState
    }
}
