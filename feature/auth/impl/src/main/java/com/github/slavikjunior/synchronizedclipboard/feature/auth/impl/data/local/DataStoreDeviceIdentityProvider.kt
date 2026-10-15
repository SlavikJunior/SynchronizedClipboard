package com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.slavikjunior.synchronizedclipboard.core.network.DeviceIdentityProvider
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single

private val Context.deviceIdentityDataStore by preferencesDataStore(name = "device_identity")

private val DEVICE_ID_KEY = stringPreferencesKey("device_id")

/**
 * Android-реализация [DeviceIdentityProvider].
 *
 * UUID устройства, выданный бэкендом при `POST /api/v1/devices`, хранится в
 * DataStore Preferences (`device_identity`). Устройство локально-идентично до
 * явного logout/переустановки — при переустановке выдается новый UUID.
 */
@Single
internal class DataStoreDeviceIdentityProvider(
    private val context: Context,
) : DeviceIdentityProvider {

    override suspend fun currentDeviceId(): String? =
        context.deviceIdentityDataStore.data.first()[DEVICE_ID_KEY]

    override suspend fun saveDeviceId(deviceId: String) {
        context.deviceIdentityDataStore.edit { preferences ->
            preferences[DEVICE_ID_KEY] = deviceId
        }
    }
}
