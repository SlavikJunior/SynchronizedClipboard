package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.system

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.AddClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.R
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TileActionActivity : ComponentActivity(), KoinComponent {
    private val addUseCase: AddClipboardItemUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clipboard = getSystemService(ClipboardManager::class.java)
        val text = clipboard?.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString()
        if (!text.isNullOrBlank()) {
            val item = ClipboardItem(
                id = System.currentTimeMillis().toString(),
                text = text,
                timestamp = System.currentTimeMillis(),
                sourceDevice = "Quick Tile",
                isPinned = false,
            )
            lifecycleScope.launch {
                addUseCase(item)
                Toast.makeText(this@TileActionActivity, R.string.toast_clipboard_added, Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            finish()
        }
    }
}
