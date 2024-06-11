package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DevicePersistenceImpl : DevicePersistence {

    private val deviceStateFlow: MutableStateFlow<DeviceState> = MutableStateFlow(DeviceState.Initial)
    private val selectedDeviceId = MutableStateFlow("")

    override fun getCachedState() = deviceStateFlow.asStateFlow()

    override fun getSelectedDeviceId() = selectedDeviceId.asStateFlow()

    override suspend fun setSelectedDeviceId(entityId: String) {
        selectedDeviceId.value = entityId
    }

    override suspend fun updateDeviceState(deviceState: DeviceState) {
        deviceStateFlow.value = deviceState
    }
}
