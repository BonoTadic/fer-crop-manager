package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceValues
import kotlinx.coroutines.flow.Flow

interface DeviceValuesPersistence {

    fun getCachedDeviceValues(): Flow<Map<String, DeviceValues>>
    suspend fun updateDeviceValues(deviceValuesMap: Map<String, DeviceValues>)
}
