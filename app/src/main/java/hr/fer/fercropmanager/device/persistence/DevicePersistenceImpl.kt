package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceState
import hr.fer.fercropmanager.device.service.DeviceValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DevicePersistenceImpl : DevicePersistence {

    private val cachedDeviceState: MutableStateFlow<DeviceState> = MutableStateFlow(DeviceState.Initial)
    private val selectedDeviceId = MutableStateFlow("")

    override fun getCachedDeviceState() = cachedDeviceState.asStateFlow()

    override fun getSelectedDeviceId() = selectedDeviceId.asStateFlow()

    override suspend fun setSelectedDeviceId(entityId: String) {
        selectedDeviceId.value = entityId
    }

    override suspend fun updateDeviceState(deviceState: DeviceState) {
        cachedDeviceState.value = deviceState
    }
}
