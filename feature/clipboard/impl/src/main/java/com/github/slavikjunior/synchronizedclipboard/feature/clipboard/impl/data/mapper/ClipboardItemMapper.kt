package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.mapper

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.data.model.ClipboardItemData

fun ClipboardItem.toData() = ClipboardItemData(id, text, timestamp, sourceDevice, isPinned)
