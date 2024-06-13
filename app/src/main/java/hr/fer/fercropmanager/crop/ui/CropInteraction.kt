package hr.fer.fercropmanager.crop.ui

import hr.fer.fercropmanager.crop.ui.plants.Plant

sealed interface CropInteraction {

    data object RetryClick : CropInteraction
    data object SprinklerClick : CropInteraction
    data object HideBottomSheet : CropInteraction
    data object ActivateSprinkler : CropInteraction
    data object LightButtonClick : CropInteraction
    data object LedStateChangeConfirm : CropInteraction
    data object PlantsSettingsClick : CropInteraction
    data object PlantsDialogClose : CropInteraction
    data class PlantsDialogConfirm(val plants: List<Plant>) : CropInteraction
    data class OnCheckedChange(val isChecked: Boolean) : CropInteraction
    data class TabChange(val index: Int, val id: String) : CropInteraction
}
