package hr.fer.fercropmanager.crop.ui

sealed interface CropInteraction {

    data object RetryClick : CropInteraction
    data object SettingsClick : CropInteraction
    data object NotificationsClick : CropInteraction
    data object StartWateringClick : CropInteraction
    data object HideBottomSheet : CropInteraction
    data object ConfirmWatering : CropInteraction
    data class TargetValueChange(val targetValue: String) : CropInteraction
    data class TabChange(val index: Int, val id: String) : CropInteraction
}