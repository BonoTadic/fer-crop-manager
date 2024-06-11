package hr.fer.fercropmanager.crop.ui

import hr.fer.fercropmanager.crop.usecase.CropState
import hr.fer.fercropmanager.crop.usecase.UserData

data class CropViewState(
    val selectedIndex: Int = 0,
    val cropState: CropState = CropState.Loaded.Loading(UserData("", "")),
    val isBottomSheetVisible: Boolean = false,
    val targetValue: String = "",
)
