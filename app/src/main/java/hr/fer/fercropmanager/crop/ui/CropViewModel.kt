package hr.fer.fercropmanager.crop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fer.fercropmanager.alarms.service.AlarmsService
import hr.fer.fercropmanager.crop.ui.plants.service.PlantsService
import hr.fer.fercropmanager.crop.ui.utils.combine
import hr.fer.fercropmanager.crop.usecase.CropUseCase
import hr.fer.fercropmanager.device.service.DeviceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CropViewModel(
    private val cropUseCase: CropUseCase,
    private val deviceService: DeviceService,
    private val plantsService: PlantsService,
    private val alarmsService: AlarmsService,
) : ViewModel() {

    private val selectedIndexFlow = MutableStateFlow(0)

    private val isSprinklerBottomSheetVisibleFlow = MutableStateFlow(false)

    private val isLedBottomSheetVisibleFlow = MutableStateFlow(false)
    private val isLedEnabledFlow = MutableStateFlow(false)

    private val isPlantsDialogVisibleFlow = MutableStateFlow(false)

    init {
        viewModelScope.launch { alarmsService.startPollingAlarms() }
    }

    val state = combine(
        selectedIndexFlow,
        cropUseCase.getCropStateFlow(),
        isSprinklerBottomSheetVisibleFlow,
        isLedBottomSheetVisibleFlow,
        isLedEnabledFlow,
        isPlantsDialogVisibleFlow,
    ) { selectedIndex, cropState, isSprinklerBottomSheetVisible,
        isLedBottomSheetVisible, isLedEnabled, isPlantsDialogVisible ->
        CropViewState(
            selectedIndex = selectedIndex,
            cropState = cropState,
            isSprinkleBottomSheetVisible = isSprinklerBottomSheetVisible,
            isLedBottomSheetVisible = isLedBottomSheetVisible,
            isLedEnabled = isLedEnabled,
            isPlantsDialogVisible = isPlantsDialogVisible,
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = CropViewState())

    fun onInteraction(interaction: CropInteraction) {
        when (interaction) {
            is CropInteraction.TabChange -> viewModelScope.launch {
                selectedIndexFlow.value = interaction.index
                deviceService.setSelectedDeviceId(interaction.id)
            }
            CropInteraction.RetryClick -> viewModelScope.launch { cropUseCase.refreshCrops() }
            CropInteraction.SettingsClick -> {
                // TODO Implement Settings screen
            }
            CropInteraction.SprinklerClick -> isSprinklerBottomSheetVisibleFlow.value = true
            CropInteraction.HideBottomSheet -> {
                isSprinklerBottomSheetVisibleFlow.value = false
                isLedBottomSheetVisibleFlow.value = false
            }
            is CropInteraction.ActivateSprinkler -> {
                isSprinklerBottomSheetVisibleFlow.value = false
                viewModelScope.launch { cropUseCase.activateSprinkler() }
            }
            CropInteraction.LightButtonClick -> {
                isLedBottomSheetVisibleFlow.value = true
            }
            CropInteraction.LedStateChangeConfirm -> {
                isLedBottomSheetVisibleFlow.value = false
                val targetValue = if (isLedEnabledFlow.value) 1 else 0
                viewModelScope.launch { cropUseCase.setLedStatus(targetValue) }
            }
            is CropInteraction.OnCheckedChange -> {
                isLedEnabledFlow.value = interaction.isChecked
            }
            CropInteraction.PlantsSettingsClick -> {
                isPlantsDialogVisibleFlow.value = true
            }
            CropInteraction.PlantsDialogClose -> {
                isPlantsDialogVisibleFlow.value = false
            }
            is CropInteraction.PlantsDialogConfirm -> viewModelScope.launch {
                plantsService.updatePlants(interaction.plants)
                isPlantsDialogVisibleFlow.value = false
            }
        }
    }
}
