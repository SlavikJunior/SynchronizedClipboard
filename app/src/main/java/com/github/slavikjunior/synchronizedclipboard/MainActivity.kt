package com.github.slavikjunior.synchronizedclipboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme
import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.AuthRoute
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.navigation.authNavEntry
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.navigation.clipboardNavEntry
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRoute
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.navigation.devicesNavEntry
import com.github.slavikjunior.synchronizedclipboard.feature.settings.api.navigation.SettingsRoute
import com.github.slavikjunior.synchronizedclipboard.feature.settings.impl.navigation.settingsNavEntry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncClipTheme {
                RootNavHost(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun RootNavHost(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(AuthRoute)

    val onNavigateToTab: (Route) -> Unit = { route ->
        if (backStack.lastOrNull() != route) {
            backStack.clear()
            backStack.add(route)
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {
            clipboardNavEntry(onNavigateToTab = onNavigateToTab)
            devicesNavEntry(onNavigateToTab = onNavigateToTab)
            settingsNavEntry(onNavigateToTab = onNavigateToTab)
            authNavEntry(
                onSignedIn = {
                    backStack.remove(AuthRoute)
                    backStack.clear()
                    backStack.add(ClipboardRoute)
                },
            )
        },
    )
}

