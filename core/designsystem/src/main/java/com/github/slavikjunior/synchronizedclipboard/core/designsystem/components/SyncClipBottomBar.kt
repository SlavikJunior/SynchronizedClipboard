package com.github.slavikjunior.synchronizedclipboard.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.slavikjunior.synchronizedclipboard.core.navigation.Route

/**
 * Переиспользуемая нижняя панель навигации (Material 3 [NavigationBar]) для экранов
 * с фиксированным набором вкладок (например, Clipboard / Devices).
 *
 * Использует composable-лямбды для иконок — поддерживает и Material-иконки,
 * и drawable-ресурсы, и кастомные painted-иконки.
 */
data class BottomNavTab(
    val route: Route,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
)

@Composable
fun SyncClipBottomBar(
    currentRoute: Route,
    tabs: List<BottomNavTab>,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            val titleRes = tab.route.tabTitleRes
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onNavigate(tab.route)
                    }
                },
                icon = {
                    if (selected) {
                        tab.selectedIcon()
                    } else {
                        tab.unselectedIcon()
                    }
                },
                label = {
                    if (titleRes != null) {
                        Text(
                            text = stringResource(id = titleRes),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                },
                alwaysShowLabel = true,
            )
        }
    }
}
