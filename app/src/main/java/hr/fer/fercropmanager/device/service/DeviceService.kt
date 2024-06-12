package hr.fer.fercropmanager.device.service

import kotlinx.coroutines.flow.Flow

interface DeviceService {

    fun getDeviceState(): Flow<DeviceState>
    fun getDeviceValues(): Flow<Map<String, DeviceValues>>
    suspend fun refreshDevices()
    suspend fun activateSprinkler(targetValue: String, onStatusChange: (RpcStatus) -> Unit)
}
