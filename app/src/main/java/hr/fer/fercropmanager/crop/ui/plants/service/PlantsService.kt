package hr.fer.fercropmanager.crop.ui.plants.service

import hr.fer.fercropmanager.crop.ui.plants.Plant
import kotlinx.coroutines.flow.Flow

interface PlantsService {

    fun getPlants(): Flow<Map<String, List<Plant>>>
    suspend fun updatePlants(plants: List<Plant>)
}
