package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceState
import kotlinx.coroutines.flow.Flow

interface DevicePersistence {

    fun getCachedState(): Flow<DeviceState>
    fun getSelectedDeviceId(): Flow<String>
    suspend fun setSelectedDeviceId(entityId: String)
    suspend fun updateDeviceState(deviceState: DeviceState)
}