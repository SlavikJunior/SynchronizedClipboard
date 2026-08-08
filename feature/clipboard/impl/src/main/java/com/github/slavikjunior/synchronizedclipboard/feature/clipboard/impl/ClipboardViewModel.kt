package com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.AddClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ClipboardItem
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.DeleteClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.ObserveClipboardUseCase
import com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api.PinClipboardItemUseCase
import com.github.slavikjunior.synchronizedclipboard.core.designsystem.state.ScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
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
                _state.value = ScreenState.Success(ClipboardState(items = items))
            }
        }
    }

    fun handleEvent(event: ClipboardEvent) {
        when (event) {
            is ClipboardEvent.OnFabClicked -> {
                viewModelScope.launch {
                    val fakeItem = ClipboardItem(
                        id = System.currentTimeMillis().toString(),
                        text = "Новый элемент (Fake)",
                        timestamp = System.currentTimeMillis(),
                        sourceDevice = "Это устройство",
                        isPinned = false,
                    )
                    addClipboardItemUseCase(fakeItem)
                    _effect.send(
                        ClipboardEffect.ShowSnackbar(
                            message = "Элемент добавлен",
                            actionLabel = "Отменить",
                            onAction = {
                                viewModelScope.launch {
                                    deleteClipboardItemUseCase(fakeItem.id)
                                }
                            },
                        )
                    )
                }
            }

            is ClipboardEvent.OnItemCopied -> {
                viewModelScope.launch {
                    _effect.send(ClipboardEffect.ShowToast("Скопировано"))
                }
            }

            is ClipboardEvent.OnItemDeleted -> {
                viewModelScope.launch {
                    val currentItems = (_state.value as? ScreenState.Success<ClipboardState>)?.data?.items
                        ?: emptyList()
                    deletedItem = currentItems.firstOrNull { it.id == event.itemId }
                    deleteClipboardItemUseCase(event.itemId)
                    _effect.send(
                        ClipboardEffect.ShowSnackbar(
                            message = "Удалено",
                            actionLabel = "Отменить",
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

            is ClipboardEvent.OnItemPinned -> {
                viewModelScope.launch {
                    pinClipboardItemUseCase(event.itemId)
                    _effect.send(ClipboardEffect.ShowToast("Закрепление изменено"))
                }
            }
        }
    }
}
