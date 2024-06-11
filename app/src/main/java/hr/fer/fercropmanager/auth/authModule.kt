package hr.fer.fercropmanager.auth

import hr.fer.fercropmanager.auth.api.AuthApi
import hr.fer.fercropmanager.auth.api.AuthApiImpl
import hr.fer.fercropmanager.auth.persistence.AuthPersistence
import hr.fer.fercropmanager.auth.persistence.AuthPersistenceImpl
import hr.fer.fercropmanager.auth.service.AuthService
import hr.fer.fercropmanager.auth.service.AuthServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    singleOf(::AuthApiImpl) bind AuthApi::class
    singleOf(::AuthServiceImpl) bind AuthService::class
    singleOf(::AuthPersistenceImpl) bind AuthPersistence::class
}
