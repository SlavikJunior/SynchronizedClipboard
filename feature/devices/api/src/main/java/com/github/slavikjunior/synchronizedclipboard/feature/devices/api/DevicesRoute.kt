package com.github.slavikjunior.synchronizedclipboard.feature.devices.api

import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.R
import kotlinx.serialization.Serializable

/**
 * Экран управления устройствами (BottomNav таб "Устройства").
 */
@Serializable
data object DevicesRoute : Route {
    override val tabTitleRes: Int? = R.string.tab_devices
}
