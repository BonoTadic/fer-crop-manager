package hr.fer.fercropmanager.device.service

import kotlinx.coroutines.flow.Flow

interface DeviceService {

    fun getDeviceState(): Flow<DeviceState>
    fun getDeviceValues(): Flow<Map<String, DeviceValues>>
    fun getSelectedDeviceId(): Flow<String>
    suspend fun refreshDevices()
    suspend fun setSelectedDeviceId(deviceId: String)
    suspend fun activateSprinkler(onStatusChange: (RpcStatus) -> Unit)
    suspend fun setLedStatus(targetValue: Int, onStatusChange: (RpcStatus) -> Unit)
}
