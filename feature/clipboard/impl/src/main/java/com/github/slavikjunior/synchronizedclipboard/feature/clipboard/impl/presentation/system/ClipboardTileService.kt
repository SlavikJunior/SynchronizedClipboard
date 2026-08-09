package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.system

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.TileService

class ClipboardTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, TileActionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE).send()
        } catch (_: PendingIntent.CanceledException) {
            // no-op: TileActionActivity зарегистрирован в манифесте, отмена крайне маловероятна
        }
    }
}
