package hr.fer.fercropmanager.crop.usecase

import kotlinx.coroutines.flow.Flow

interface CropUseCase {

    fun getCropStateFlow(): Flow<CropState>
    suspend fun activateSprinkler()
    suspend fun refreshCrops()
    suspend fun setLedStatus(targetValue: Int)
}
