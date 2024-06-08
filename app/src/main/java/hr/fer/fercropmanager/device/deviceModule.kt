package hr.fer.fercropmanager.device

import hr.fer.fercropmanager.device.api.DeviceApi
import hr.fer.fercropmanager.device.api.DeviceApiImpl
import hr.fer.fercropmanager.device.persistence.DevicePersistence
import hr.fer.fercropmanager.device.persistence.DevicePersistenceImpl
import hr.fer.fercropmanager.device.service.DeviceService
import hr.fer.fercropmanager.device.service.DeviceServiceImpl
import hr.fer.fercropmanager.device.websocket.DeviceWebSocket
import hr.fer.fercropmanager.device.websocket.DeviceWebSocketImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val deviceModule = module {
    singleOf(::DeviceApiImpl) bind DeviceApi::class
    singleOf(::DeviceWebSocketImpl) bind DeviceWebSocket::class
    singleOf(::DeviceServiceImpl) bind DeviceService::class
    singleOf(::DevicePersistenceImpl) bind DevicePersistence::class
}