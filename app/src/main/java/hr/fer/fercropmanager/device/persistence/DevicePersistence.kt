package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceState
import kotlinx.coroutines.flow.Flow

interface DevicePersistence {

    fun getCachedDeviceState(): Flow<DeviceState>
    fun getSelectedDeviceId(): Flow<String>
    suspend fun updateDeviceState(deviceState: DeviceState)
    suspend fun setSelectedDeviceId(deviceId: String)
}
