package com.cs407.badgermate.data.event

import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventRepository(
    private val eventDao: EventDao
) {
    // UW events 的 iCal 地址（你可以从浏览器复制实际链接替换下边这个）
    private val icsUrl = "https://today.wisc.edu/events.ics"

    fun getAllEvents(): List<EventEntity> = eventDao.getAllEvents()

    fun insertEvent(event: EventEntity) = eventDao.insertEvent(event)

    fun setInterested(event: EventEntity, flag: Boolean) =
        eventDao.updateInterested(event.id, flag)

    /**
     * 从官网的 .ics 拉取活动，并覆盖写入本地数据库
     * 建议在 Dispatchers.IO 中调用
     */
    fun syncFromIcs() {
        val text = downloadIcs(icsUrl)
        if (text.isBlank()) return

        val parsed = IcsParser.parse(text)
        val displayFmt = SimpleDateFormat("MMM d • h:mm a", Locale.US)

        val entities = parsed.map { ev ->
            val start = Date(ev.startMillis)
            val end = Date(ev.endMillis)

            val displayTime = if (isSameDay(start, end)) {
                "${displayFmt.format(start)} - ${displayFmt.format(end)}"
            } else {
                "${displayFmt.format(start)} → ${displayFmt.format(end)}"
            }

            EventEntity(
                title = ev.summary,
                organization = "UW–Madison",
                category = "Academic", // 先统一写一个类别，后面再细分
                startTime = ev.startMillis,
                endTime = ev.endMillis,
                displayTime = displayTime,
                location = ev.location.ifBlank { "TBD" },
                isMyEvent = false
            )
        }

        eventDao.clearAll()
        if (entities.isNotEmpty()) {
            eventDao.insertAll(entities)
        }
    }

    // ----------- 辅助函数 -----------

    private fun downloadIcs(urlString: String): String {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            conn.requestMethod = "GET"
            conn.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun isSameDay(a: Date, b: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.US)
        return fmt.format(a) == fmt.format(b)
    }
}
