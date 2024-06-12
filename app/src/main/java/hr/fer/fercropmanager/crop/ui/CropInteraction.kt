package hr.fer.fercropmanager.crop.ui

sealed interface CropInteraction {

    data object RetryClick : CropInteraction
    data object SettingsClick : CropInteraction
    data object SprinklerClick : CropInteraction
    data object HideBottomSheet : CropInteraction
    data object ActivateSprinkler : CropInteraction
    data object LightButtonClick : CropInteraction
    data object LedStateChangeConfirm : CropInteraction
    data class OnCheckedChange(val isChecked: Boolean) : CropInteraction
    data class TabChange(val index: Int, val id: String) : CropInteraction
}
