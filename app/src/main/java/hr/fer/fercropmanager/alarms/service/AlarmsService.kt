package hr.fer.fercropmanager.alarms.service

import kotlinx.coroutines.flow.Flow

interface AlarmsService {

    fun getAlarmsState(): Flow<AlarmsState>
}