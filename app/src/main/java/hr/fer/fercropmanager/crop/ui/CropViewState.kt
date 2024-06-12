package hr.fer.fercropmanager.crop.ui

import hr.fer.fercropmanager.crop.usecase.CropState
import hr.fer.fercropmanager.crop.usecase.UserData

data class CropViewState(
    val selectedIndex: Int = 0,
    val cropState: CropState = CropState.Loaded.Loading(UserData("", "")),
    val isSprinkleBottomSheetVisible: Boolean = false,
    val isLedBottomSheetVisible: Boolean = false,
    val isLedEnabled: Boolean = false,
    val isPlantsDialogVisible: Boolean = false,
)
