package hr.fer.fercropmanager.device.service

sealed interface RpcStatus {

    data object Loading : RpcStatus
    data object Success : RpcStatus
    data object Error : RpcStatus
}
