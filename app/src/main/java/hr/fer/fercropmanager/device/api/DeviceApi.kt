package hr.fer.fercropmanager.device.api

interface DeviceApi {

    suspend fun getDevices(token: String, customerId: String): Result<DeviceDto>
    suspend fun activateSprinkler(token: String, entityId: String): Result<Unit>
    suspend fun setLedStatus(token: String, entityId: String, targetValue: Int): Result<Unit>
}
