package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.AddClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.DeleteClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ObserveClipboardUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.PinClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.R
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.event.ClipboardEvent
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.effect.ClipboardEffect
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl.presentation.clipboard.model.ClipboardState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
internal class ClipboardViewModel(
    private val observeClipboardUseCase: ObserveClipboardUseCase,
    private val addClipboardItemUseCase: AddClipboardItemUseCase,
    private val deleteClipboardItemUseCase: DeleteClipboardItemUseCase,
    private val pinClipboardItemUseCase: PinClipboardItemUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState<ClipboardState>>(ScreenState.Loading)
    val state: StateFlow<ScreenState<ClipboardState>> = _state

    private val _effect = Channel<ClipboardEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var deletedItem: ClipboardItem? = null

    init {
        viewModelScope.launch {
            observeClipboardUseCase().collect { items ->
                _state.update { ScreenState.Success(ClipboardState(items = items)) }
            }
        }
    }

    fun handleEvent(event: ClipboardEvent) {
        when (event) {
            is ClipboardEvent.OnFabClicked -> onFabClicked(event.text)
            is ClipboardEvent.OnItemCopied -> onItemCopied(event.itemId)
            is ClipboardEvent.OnItemDeleted -> onItemDeleted(event.itemId)
            is ClipboardEvent.OnItemPinned -> onItemPinned(event.itemId)
            ClipboardEvent.Retry -> onRetry()
        }
    }

    /**
     * Добавляет в облако текущее содержимое системного буфера обмена.
     *
     * [text] уже прочитан UI-слоем — ViewModel остаётся свободной от `Context`.
     * Пустой буфер — не ошибка, а информационный snackbar.
     */
    private fun onFabClicked(text: String?) {
        viewModelScope.launch {
            if (text.isNullOrBlank()) {
                _effect.send(
                    ClipboardEffect.ShowSnackbar(
                        messageRes = R.string.clipboard_device_empty,
                        actionLabelRes = null,
                        onAction = null,
                    )
                )
                return@launch
            }

            val item = ClipboardItem(
                id = System.currentTimeMillis().toString(),
                text = text,
                timestamp = System.currentTimeMillis(),
                sourceDevice = SOURCE_THIS_DEVICE,
                isPinned = false,
            )
            addClipboardItemUseCase(item)
            _effect.send(
                ClipboardEffect.ShowSnackbar(
                    messageRes = R.string.clipboard_added,
                    actionLabelRes = R.string.clipboard_undo,
                    onAction = {
                        viewModelScope.launch {
                            deleteClipboardItemUseCase(item.id)
                        }
                    },
                )
            )
        }
    }

    private fun onItemCopied(itemId: String) {
        viewModelScope.launch {
            _effect.send(
                ClipboardEffect.ShowSnackbar(
                    messageRes = R.string.clipboard_copied,
                    actionLabelRes = null,
                    onAction = null,
                )
            )
        }
    }

    private fun onItemDeleted(itemId: String) {
        viewModelScope.launch {
            val currentItems = (_state.value as? ScreenState.Success<ClipboardState>)?.data?.items
                ?: emptyList()
            deletedItem = currentItems.firstOrNull { it.id == itemId }
            deleteClipboardItemUseCase(itemId)
            _effect.send(
                ClipboardEffect.ShowSnackbar(
                    messageRes = R.string.clipboard_deleted,
                    actionLabelRes = R.string.clipboard_undo,
                    onAction = {
                        viewModelScope.launch {
                            deletedItem?.let { addClipboardItemUseCase(it) }
                            deletedItem = null
                        }
                    },
                )
            )
        }
    }

    private fun onItemPinned(itemId: String) {
        viewModelScope.launch {
            pinClipboardItemUseCase(itemId)
            _effect.send(
                ClipboardEffect.ShowSnackbar(
                    messageRes = R.string.clipboard_pinned_changed,
                    actionLabelRes = null,
                    onAction = null,
                )
            )
        }
    }

    private fun onRetry() {
        viewModelScope.launch {
            _state.update { ScreenState.Loading }
            observeClipboardUseCase().firstOrNull()?.let { items ->
                _state.update { ScreenState.Success(ClipboardState(items = items)) }
            } ?: _state.update { ScreenState.Error("Не удалось загрузить данные") }
        }
    }

    private companion object {
        /** Источник для элементов, скопированных с этого устройства через FAB. */
        const val SOURCE_THIS_DEVICE = "This device"
    }
}
