package hr.fer.fercropmanager.device.persistence

import hr.fer.fercropmanager.device.service.DeviceValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeviceValuesPersistenceImpl : DeviceValuesPersistence {

    private val cachedDeviceValuesMap = MutableStateFlow(mapOf<String, DeviceValues>())

    override fun getCachedDeviceValues() = cachedDeviceValuesMap.asStateFlow()

    override suspend fun updateDeviceValues(deviceValuesMap: Map<String, DeviceValues>) {
        cachedDeviceValuesMap.value = deviceValuesMap
    }
}
