package hr.fer.fercropmanager.crop.ui.plants.persistence

import hr.fer.fercropmanager.crop.ui.plants.Plant
import kotlinx.coroutines.flow.Flow

interface PlantsPersistence {

    fun getCachedPlants(): Flow<Map<String, List<Plant>>>
    suspend fun updatePlants(deviceId: String, plants: List<Plant>)
}
