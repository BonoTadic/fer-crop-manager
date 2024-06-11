package hr.fer.fercropmanager.crop.usecase

import kotlinx.coroutines.flow.Flow

interface CropUseCase {

    fun getCropStateFlow(): Flow<CropState>
    suspend fun startWatering(targetValue: String)
    suspend fun refreshCrops()
}
