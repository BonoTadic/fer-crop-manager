package hr.fer.fercropmanager.crop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fer.fercropmanager.crop.usecase.CropUseCase
import hr.fer.fercropmanager.device.persistence.DevicePersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CropViewModel(
    private val cropUseCase: CropUseCase,
    private val devicePersistence: DevicePersistence,
) : ViewModel() {

    private val selectedIndexFlow = MutableStateFlow(0)
    private val isBottomSheetVisibleFlow = MutableStateFlow(false)
    private val targetValueFlow = MutableStateFlow("")

    val state = combine(
        selectedIndexFlow,
        cropUseCase.getCropStateFlow(),
        isBottomSheetVisibleFlow,
        targetValueFlow,
    ) { selectedIndex, cropState, isBottomSheetVisible, targetValue ->
        CropViewState(selectedIndex, cropState, isBottomSheetVisible, targetValue)
    }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = CropViewState())

    fun onInteraction(interaction: CropInteraction) {
        when (interaction) {
            is CropInteraction.TabChange -> viewModelScope.launch {
                selectedIndexFlow.value = interaction.index
                devicePersistence.setSelectedDeviceId(interaction.id)
            }
            CropInteraction.RetryClick -> viewModelScope.launch { cropUseCase.refreshCrops() }
            CropInteraction.SettingsClick -> {
                // TODO Implement Settings screen
            }
            CropInteraction.NotificationsClick -> {
                // TODO Implement fetching and handling of notifications
            }
            CropInteraction.StartWateringClick -> isBottomSheetVisibleFlow.value = true
            CropInteraction.HideBottomSheet -> {
                isBottomSheetVisibleFlow.value = false
                targetValueFlow.value = ""
            }
            is CropInteraction.ConfirmWatering -> {
                isBottomSheetVisibleFlow.value = false
                viewModelScope.launch { cropUseCase.startWatering(targetValueFlow.value) }
                targetValueFlow.value = ""
            }
            is CropInteraction.TargetValueChange -> {
                targetValueFlow.value = interaction.targetValue
            }
        }
    }
}