package hr.fer.fercropmanager.crop.ui.plants.persistence

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import hr.fer.fercropmanager.crop.ui.plants.Plant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "plants_prefs")
private val PLANTS_KEY = stringPreferencesKey("plants_key")

class PlantsPersistenceImpl(private val context: Context) : PlantsPersistence {

    private var cachedPlantsMap = MutableStateFlow(mapOf<String, List<Plant>>())

    init {
        loadCachedPlants()
    }

    override fun getCachedPlants() = cachedPlantsMap.asStateFlow()

    override suspend fun updatePlants(deviceId: String, plants: List<Plant>) {
        val updatedMap = cachedPlantsMap.value.toMutableMap().apply {
            put(deviceId, plants)
        }.toMap()
        cachedPlantsMap.value = updatedMap
        savePlantsToDataStore(updatedMap)
    }

    private suspend fun savePlantsToDataStore(plantsMap: Map<String, List<Plant>>) {
        val jsonString = Json.encodeToString(plantsMap)
        context.dataStore.edit { preferences ->
            preferences[PLANTS_KEY] = jsonString
        }
    }

    private fun loadCachedPlants() {
        context.dataStore.data
            .map { preferences ->
                preferences[PLANTS_KEY]?.let { jsonString ->
                    Json.decodeFromString<Map<String, List<Plant>>>(jsonString)
                } ?: emptyMap()
            }
            .onEach { loadedMap ->
                cachedPlantsMap.value = loadedMap
            }
            .launchIn(scope = CoroutineScope(Dispatchers.IO))
    }
}
