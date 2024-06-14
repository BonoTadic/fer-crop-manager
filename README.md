FER Crop Manager

An MVVM application for managing user's crops.

The app is written in Kotlin using Jetpack Compose. It implements the following techonolgies: Kotlin Coroutines for working with async methods, Flows for collecting data for the UI layer, Material 3 library for UI components, Koin for dependency injection, Ktor client for creating HTTP requests and web-sockets, DataStore for persistence and KotlinX serialization for Json parsing. 

App features:
- ThingsBoard authentication
- Token persistence (DataStore)
- Devices fetch (REST API)
- Sensor data fetch: temperature, humidity and moisture (webs-sockets)
- Manually adjusting plants for specific crop
- Sending RPC command for activating sprinklers
- Sendinc RPC command for turning an LED on or off
- Fetching and clearing alarms
- Displaying user information
