package hr.fer.fercropmanager.snackbar

import hr.fer.fercropmanager.snackbar.service.SnackbarService
import hr.fer.fercropmanager.snackbar.service.SnackbarServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val snackbarModule = module {
    singleOf(::SnackbarServiceImpl) bind SnackbarService::class
    singleOf(::SnackbarManager)
}