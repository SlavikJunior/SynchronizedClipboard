package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.system

import android.content.Intent
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

class ShareActivity : ComponentActivity(), KoinComponent {
    private val addUseCase: AddClipboardItemUseCase by inject()
    private companion object {
        const val MAX_TEXT_LENGTH = 100_000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        if (text.isNotBlank()) {
            val processed = if (text.length > MAX_TEXT_LENGTH) {
                Toast.makeText(this, R.string.clipboard_text_truncated, Toast.LENGTH_SHORT).show()
                text.substring(0, MAX_TEXT_LENGTH)
            } else {
                text
            }
            val item = ClipboardItem(
                id = System.currentTimeMillis().toString(),
                text = processed,
                timestamp = System.currentTimeMillis(),
                sourceDevice = "Share",
                isPinned = false,
            )
            lifecycleScope.launch {
                addUseCase(item)
                Toast.makeText(this@ShareActivity, R.string.toast_clipboard_added, Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            finish()
        }
    }
}
