package hr.fer.fercropmanager.login

import hr.fer.fercropmanager.login.service.LoginService
import hr.fer.fercropmanager.login.service.LoginServiceImpl
import hr.fer.fercropmanager.login.ui.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val loginModule = module {
    singleOf(::LoginServiceImpl) bind LoginService::class
    viewModelOf(::LoginViewModel)
}