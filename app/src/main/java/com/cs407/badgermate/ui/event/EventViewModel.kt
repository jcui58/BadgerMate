package com.cs407.badgermate.ui.event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cs407.badgermate.data.AppDatabase
import com.cs407.badgermate.data.event.EventEntity
import com.cs407.badgermate.data.event.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = EventRepository(
        AppDatabase.getInstance(application).eventDao()
    )

    private val _events = MutableLiveData<List<EventEntity>>(emptyList())
    val events: LiveData<List<EventEntity>> = _events

    init {
        refresh()
    }

    fun refresh() {
        _events.value = repo.getAllEvents()
    }

    fun addEvent(event: EventEntity) {
        repo.insertEvent(event)
        refresh()
    }

    fun toggleInterested(event: EventEntity) {
        val newFlag = !event.isMyEvent
        repo.setInterested(event, newFlag)
        refresh()
    }

    /** 从官网同步 .ics，然后刷新列表 */
    fun syncFromIcs() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.syncFromIcs()
            val list = repo.getAllEvents()
            withContext(Dispatchers.Main) {
                _events.value = list
            }
        }
    }

    /** 顶部统计用：只返回 Upcoming 和 Interested 数量 */
    fun calcStats(list: List<EventEntity>? = _events.value): Pair<Int, Int> {
        val l = list ?: emptyList()
        val now = System.currentTimeMillis()
        val upcoming = l.count { it.startTime > now }
        val interested = l.count { it.isMyEvent }
        return upcoming to interested
    }
}
