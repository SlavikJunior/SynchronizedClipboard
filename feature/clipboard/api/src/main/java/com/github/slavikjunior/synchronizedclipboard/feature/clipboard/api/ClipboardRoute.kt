package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api

import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.R
import kotlinx.serialization.Serializable

@Serializable
data object ClipboardRoute : Route {
    override val tabTitleRes: Int? = R.string.tab_clipboard
}
