package hr.fer.fercropmanager.alarms.api

import kotlinx.serialization.Serializable

@Serializable
data class AlarmsDto(
    val data: List<Data>,
    val hasNext: Boolean,
    val totalElements: Int,
    val totalPages: Int
)

@Serializable
data class Data(
    val ackTs: Long,
    val clearTs: Long,
    val createdTime: Long,
    val customerId: CustomerId,
    val endTs: Long,
    val id: Id,
    val name: String,
    val originator: Originator,
    val propagate: Boolean,
    val propagateToOwner: Boolean,
    val propagateToTenant: Boolean,
    val severity: String,
    val startTs: Long,
    val status: String,
    val tenantId: TenantId,
    val type: String,
    val details: Details? = null,
    val originatorName: String? = null,
    val propagateRelationTypes: List<String>? = null,
)

@Serializable
data class CustomerId(
    val entityType: String,
    val id: String
)

@Serializable
data class Id(
    val entityType: String,
    val id: String
)

@Serializable
data class Originator(
    val entityType: String,
    val id: String
)

@Serializable
data class TenantId(
    val entityType: String,
    val id: String
)

@Serializable
data class AlarmDto(
    val ackTs: Long,
    val acknowledged: Boolean,
    val assignTs: Long,
    val assigneeId: AssigneeId,
    val clearTs: Long,
    val cleared: Boolean,
    val createdTime: Long,
    val customerId: CustomerId,
    val details: Details,
    val endTs: Long,
    val id: Id,
    val name: String,
    val originator: Originator,
    val propagate: Boolean,
    val propagateRelationTypes: List<String>,
    val propagateToOwner: Boolean,
    val propagateToTenant: Boolean,
    val severity: String,
    val startTs: Long,
    val status: String,
    val tenantId: TenantId,
    val type: String
)

@Serializable
data class AssigneeId(
    val entityType: String,
    val id: String
)

@Serializable
class Details