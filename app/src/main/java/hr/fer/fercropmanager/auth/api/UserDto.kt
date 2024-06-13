package hr.fer.fercropmanager.auth.api

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val additionalInfo: AdditionalInfo,
    val authority: String,
    val createdTime: Long,
    val customerId: CustomerId,
    val email: String,
    val id: Id,
    val name: String,
    val tenantId: TenantId,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
)

@Serializable
class AdditionalInfo

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
data class TenantId(
    val entityType: String,
    val id: String
)
