package hr.fer.fercropmanager.device.service

import kotlinx.coroutines.flow.Flow

interface DeviceService {

    fun getDeviceState(): Flow<DeviceState>
    suspend fun refreshDeviceState()
    suspend fun startActuation(targetValue: String)
}