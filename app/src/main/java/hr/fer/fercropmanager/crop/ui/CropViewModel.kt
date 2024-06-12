package hr.fer.fercropmanager.crop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.fer.fercropmanager.crop.usecase.CropUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CropViewModel(private val cropUseCase: CropUseCase) : ViewModel() {

    private val selectedIndexFlow = MutableStateFlow(0)

    private val isSprinklerBottomSheetVisibleFlow = MutableStateFlow(false)

    private val isLedBottomSheetVisibleFlow = MutableStateFlow(false)
    private val isLedEnabledFlow = MutableStateFlow(false)

    val state = combine(
        selectedIndexFlow,
        cropUseCase.getCropStateFlow(),
        isSprinklerBottomSheetVisibleFlow,
        isLedBottomSheetVisibleFlow,
        isLedEnabledFlow,
    ) { selectedIndex, cropState, isSprinklerBottomSheetVisible, isLedBottomSheetVisible, isLedEnabled ->
        CropViewState(
            selectedIndex = selectedIndex,
            cropState = cropState,
            isSprinkleBottomSheetVisible = isSprinklerBottomSheetVisible,
            isLedBottomSheetVisible = isLedBottomSheetVisible,
            isLedEnabled = isLedEnabled,
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = CropViewState())

    fun onInteraction(interaction: CropInteraction) {
        when (interaction) {
            is CropInteraction.TabChange -> viewModelScope.launch {
                selectedIndexFlow.value = interaction.index
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
        }
    }
}
