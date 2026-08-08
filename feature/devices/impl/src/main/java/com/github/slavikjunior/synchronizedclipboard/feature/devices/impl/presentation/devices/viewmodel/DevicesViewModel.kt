package com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DeviceItem
import com.github.slavikjunior.synchronizedclipboard.feature.devices.api.DevicesRepository
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.R
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.event.DevicesEvent
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.effect.DevicesEffect
import com.github.slavikjunior.synchronizedclipboard.feature.devices.impl.presentation.devices.model.DevicesState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
internal class DevicesViewModel(
    private val repository: DevicesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState<DevicesState>>(ScreenState.Loading)
    val state: StateFlow<ScreenState<DevicesState>> = _state

    private val _effect = Channel<DevicesEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.observeDevices()
                .catch { error ->
                    _state.value = ScreenState.Error(error.message ?: "Ошибка загрузки устройств")
                }
                .collect { devices ->
                    if (devices.isEmpty()) {
                        _state.value = ScreenState.Empty("Нет привязанных устройств")
                    } else {
                        _state.value = ScreenState.Success(DevicesState(devices = devices))
                    }
                }
        }
    }

    fun handleEvent(event: DevicesEvent) {
        when (event) {
            is DevicesEvent.OnUnlinkClicked -> onUnlinkClicked(event.device)
        }
    }

    private fun onUnlinkClicked(device: DeviceItem) {
        if (device.isCurrentDevice) {
            viewModelScope.launch {
                _effect.send(DevicesEffect.CannotUnlinkCurrent)
            }
            return
        }

        viewModelScope.launch {
            val result = repository.unlinkDevice(device.id)
            if (result.isSuccess) {
                _effect.send(DevicesEffect.Unlinked(deviceName = device.name))
            } else {
                _effect.send(DevicesEffect.UnlinkFailed)
            }
        }
    }
}
