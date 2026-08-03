package com.github.slavikjunior.synchronizedclipboard.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SyncClipPrimary,
    onPrimary = SyncClipOnPrimary,
    secondary = SyncClipSecondary,
    tertiary = SyncClipTertiary,
    background = SyncClipBackground,
    surface = SyncClipSurface,
    onSurface = SyncClipOnSurface,
    error = SyncClipError,
)

private val DarkColorScheme = darkColorScheme(
    primary = SyncClipPrimaryDark,
    onPrimary = SyncClipOnPrimaryDark,
    secondary = SyncClipSecondaryDark,
    tertiary = SyncClipTertiaryDark,
    background = SyncClipBackgroundDark,
    surface = SyncClipSurfaceDark,
    onSurface = SyncClipOnSurfaceDark,
    error = SyncClipErrorDark,
)

/**
 * Единая тема приложения SynchronizedClipboard.
 *
 * Все feature-модули обязаны оборачивать экраны именно в этот Theme
 * (контракт AGENTS.md): «All UI components via :core:designsystem — never raw Material».
 *
 * @param darkTheme следовать системной тёмной теме (по умолчанию — да).
 * @param dynamicColor использовать Material You (Android 12+). По умолчанию — true.
 */
@Composable
fun SyncClipTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SyncClipTypography,
        content = content,
    )
}
