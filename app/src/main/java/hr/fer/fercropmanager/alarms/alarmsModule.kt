package hr.fer.fercropmanager.alarms

import hr.fer.fercropmanager.alarms.api.AlarmsApi
import hr.fer.fercropmanager.alarms.api.AlarmsApiImpl
import hr.fer.fercropmanager.alarms.persistence.AlarmsPersistence
import hr.fer.fercropmanager.alarms.persistence.AlarmsPersistenceImpl
import hr.fer.fercropmanager.alarms.service.AlarmsService
import hr.fer.fercropmanager.alarms.service.AlarmsServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val alertsModule = module {
    singleOf(::AlarmsApiImpl) bind AlarmsApi::class
    singleOf(::AlarmsPersistenceImpl) bind AlarmsPersistence::class
    singleOf(::AlarmsServiceImpl) bind AlarmsService::class
}