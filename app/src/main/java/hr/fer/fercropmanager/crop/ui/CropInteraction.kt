package hr.fer.fercropmanager.crop.ui

sealed interface CropInteraction {

    data object RetryClick : CropInteraction
    data object SettingsClick : CropInteraction
    data object StartSprinklerClick : CropInteraction
    data object HideBottomSheet : CropInteraction
    data object ActivateSprinkler : CropInteraction
    data class TabChange(val index: Int, val id: String) : CropInteraction
}
