package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.model

import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun ClipboardItem.formattedTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
