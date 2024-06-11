package hr.fer.fercropmanager.device.api

interface DeviceApi {

    suspend fun getDevices(token: String, customerId: String): Result<DeviceDto>
    suspend fun startActuation(token: String, entityId: String, targetValue: String): Result<Unit>
}
