package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.data.mapper

import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.data.model.DeviceItemData

fun DeviceItem.toData() = DeviceItemData(id, name, os, isCurrentDevice, isOnline, lastSyncTimestamp)
