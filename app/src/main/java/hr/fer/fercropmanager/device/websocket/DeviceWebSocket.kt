package hr.fer.fercropmanager.device.websocket

interface DeviceWebSocket {

    fun retrieveDeviceId(subscriptionId: Int): String
    suspend fun connectWebSocket(token: String, entityIdList: List<String>, onDataReceived: (String?) -> Unit)
}