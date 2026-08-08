package com.github.slavikjunior.synchronizedclipboard.feature.settings.api.navigation

import androidx.navigation3.runtime.NavKey
import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.R
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute : Route {
    override val tabTitleRes: Int? = R.string.tab_settings
}
