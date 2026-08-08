package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api

import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data object ClipboardRoute : Route {
    override val tabTitle: String = "Буфер"
}
