package hr.fer.fercropmanager.crop.ui.utils

internal fun Float.formatFloat(): String {
    return "%.2f".format(this)
}