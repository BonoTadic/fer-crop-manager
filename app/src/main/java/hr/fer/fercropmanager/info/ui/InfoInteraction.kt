package hr.fer.fercropmanager.info.ui

sealed interface InfoInteraction {

    data object LogoutClick : InfoInteraction
    data object LogoutConfirm : InfoInteraction
    data object DialogHide : InfoInteraction
}
