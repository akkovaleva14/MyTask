package com.example.mytask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<RecyclerViewItem>>(emptyList())
    val items: StateFlow<List<RecyclerViewItem>> = _items

    private val originalItems = listOf(
        RecyclerViewItem.Header("Header Cats"),
        RecyclerViewItem.Card(
            "https://cdn.britannica.com/34/235834-050-C5843610/two-different-breeds-of-cats-side-by-side-outdoors-in-the-garden.jpg",
            "Cats"
        ),
        RecyclerViewItem.Card(
            "https://images.unsplash.com/photo-1542397284385-6010376c5337?q=80&w=3474&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "Heavy"
        ),
        RecyclerViewItem.Card(
            "https://248006.selcdn.ru/main/iblock/96a/96a51feeb75ec92a8fc193287d9a8ca8/be16e663a4c7c5126011378f04d6a797.jpg",
            "Oh no"
        ),
        RecyclerViewItem.Card(
            "https://images.squarespace-cdn.com/content/v1/54822a56e4b0b30bd821480c/51fe71a3-cb12-4ac2-882f-45955401dd53/Golden+Retrievers+dans+pet+care.jpeg",
            "Dog"
        ),
        RecyclerViewItem.Header("Header Bird"),
        RecyclerViewItem.Card(
            "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Eopsaltria_australis_-_Mogo_Campground.jpg/2880px-Eopsaltria_australis_-_Mogo_Campground.jpg",
            "Bird"
        ),
        RecyclerViewItem.Header("Header Hedgehog"),
        RecyclerViewItem.Card(
            "https://vetmed.illinois.edu/wp-content/uploads/2021/04/pc-keller-hedgehog.jpg",
            "Hedgehog"
        ),
        RecyclerViewItem.Card(
            "https://www.granturismoevents.com/_Pages/3331/Image920x600.jpg",
            "Dolphin"
        ),
    )

    init {
        _items.value = originalItems
    }

    fun refreshItems(onComplete: (List<RecyclerViewItem>) -> Unit) {
        viewModelScope.launch {
            delay(2000)

            var updatedCount = 0
            val updatedItems = originalItems.mapIndexed { index, item ->
                if (updatedCount < 2) {
                    updatedCount++
                    when (item) {
                        is RecyclerViewItem.Header -> item.copy(title = "(updated) ${item.title}")
                        is RecyclerViewItem.Card -> item.copy(description = "(updated) ${item.description}")
                    }.also { updatedItem ->
                        // Уведомляем адаптер о частичном обновлении
                        _items.value.let { currentItems ->
                            if (currentItems[index] != updatedItem) {
                                // Обновляем элемент в списке
                                _items.value =
                                    currentItems.toMutableList().apply { this[index] = updatedItem }
                                // Передаем обновленный элемент
                                onComplete(listOf(updatedItem))
                            }
                        }
                    }
                } else {
                    item // Возвращаем элемент без изменений, если уже обновлено 2
                }
            }

            // Уведомляем об обновлении списка
            _items.value = updatedItems
            // Вызываем callback для передачи обновленного списка
            onComplete(updatedItems)
        }
    }
}
