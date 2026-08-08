package com.github.slavikjunior.synchronizedclipboard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme.SyncClipTheme
import com.github.slavikjunior.synchronizedclipboard.feature.auth.api.AuthRoute
import com.github.slavikjunior.synchronizedclipboard.feature.auth.impl.navigation.authNavEntry
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardRoute
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.navigation.clipboardNavEntry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncClipTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    RootNavHost(
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun RootNavHost(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(AuthRoute)
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {
            clipboardNavEntry()
            authNavEntry(
                onSignedIn = {
                    backStack.clear()
                    backStack.add(ClipboardRoute)
                },
            )
        },
    )
}
