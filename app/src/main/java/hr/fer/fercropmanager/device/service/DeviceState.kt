package hr.fer.fercropmanager.device.service

sealed interface DeviceState {

    data object Initial : DeviceState
    data object Loading : DeviceState
    data object Error : DeviceState

    sealed interface Loaded : DeviceState {
        data object Empty : Loaded
        data class Available(val devices: List<Device>, val isShortcutLoading: Boolean = false) : Loaded
    }
}

data class Device(
    val id: String,
    val name: String,
    val temperature: Float,
    val humidity: Float,
    val moisture: Float,
    val isWateringInProgress: Boolean,
)
