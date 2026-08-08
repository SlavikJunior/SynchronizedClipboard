package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api

import kotlinx.serialization.Serializable

@Serializable
data class ClipboardItem(
    val id: String,
    val text: String,
    val timestamp: Long,
    val sourceDevice: String,
    val isPinned: Boolean = false,
)
