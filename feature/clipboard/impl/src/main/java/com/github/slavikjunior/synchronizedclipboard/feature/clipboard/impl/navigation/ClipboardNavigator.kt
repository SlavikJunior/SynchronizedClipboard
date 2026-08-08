package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.ui.ClipboardScreen

fun EntryProviderScope<NavKey>.clipboardNavEntry() {
    entry(ClipboardRoute) {
        ClipboardScreen()
    }
}
