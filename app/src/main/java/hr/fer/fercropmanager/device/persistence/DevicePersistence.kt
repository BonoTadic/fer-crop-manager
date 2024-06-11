package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceState
import hr.fer.fercropmanager.device.service.DeviceValues
import kotlinx.coroutines.flow.Flow

interface DevicePersistence {

    fun getCachedDeviceState(): Flow<DeviceState>
    fun getSelectedDeviceId(): Flow<String>
    suspend fun setSelectedDeviceId(entityId: String)
    suspend fun updateDeviceState(deviceState: DeviceState)

}
