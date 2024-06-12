package hr.fer.fercropmanager.crop.ui.plants.persistence

import hr.fer.fercropmanager.crop.ui.plants.Plant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlantsPersistenceImpl : PlantsPersistence {

    private var cachedPlantsMap = MutableStateFlow(mapOf<String, List<Plant>>())

    override fun getCachedPlants() = cachedPlantsMap.asStateFlow()

    override suspend fun updatePlants(deviceId: String, plants: List<Plant>) {
        val updatedMap = cachedPlantsMap.value.toMutableMap().apply {
            put(deviceId, plants)
        }.toMap()
        cachedPlantsMap.value = updatedMap
    }
}
