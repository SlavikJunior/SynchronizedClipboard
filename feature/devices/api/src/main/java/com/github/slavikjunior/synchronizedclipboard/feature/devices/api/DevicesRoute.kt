package com.github.slavikjunior.synchronizedclipboard.feature.devices.api

import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import kotlinx.serialization.Serializable

/**
 * Экран управления устройствами (BottomNav таб "Устройства").
 */
@Serializable
data object DevicesRoute : Route {
    override val tabTitle: String = "Устройства"
}
