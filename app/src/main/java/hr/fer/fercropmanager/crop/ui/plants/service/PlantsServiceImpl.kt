package hr.fer.fercropmanager.crop.ui.plants.service

import hr.fer.fercropmanager.crop.ui.plants.Plant
import hr.fer.fercropmanager.crop.ui.plants.persistence.PlantsPersistence
import hr.fer.fercropmanager.device.service.DeviceService
import kotlinx.coroutines.flow.first

class PlantsServiceImpl(
    private val plantsPersistence: PlantsPersistence,
    private val deviceService: DeviceService,
) : PlantsService {

    override fun getPlants() = plantsPersistence.getCachedPlants()

    override suspend fun updatePlants(plants: List<Plant>) {
        val selectedDeviceId = deviceService.getSelectedDeviceId().first()
        plantsPersistence.updatePlants(selectedDeviceId, plants)
    }
}
