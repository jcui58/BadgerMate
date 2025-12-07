package com.cs407.badgermate.data.event

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 负责把 .ics 文本解析成简单的事件列表
 */
object IcsParser {

    data class IcsEvent(
        val summary: String,
        val location: String,
        val startMillis: Long,
        val endMillis: Long,
        val url: String?,
        val description: String?
    )

    fun parse(icsText: String): List<IcsEvent> {
        val result = mutableListOf<IcsEvent>()
        val lines = icsText.lines()

        var inEvent = false

        var summary = ""
        var location = ""
        var dtStart: String? = null
        var dtEnd: String? = null
        var url: String? = null
        var desc: String? = null

        for (raw in lines) {
            val line = raw.trim()
            when {
                line == "BEGIN:VEVENT" -> {
                    inEvent = true
                    summary = ""
                    location = ""
                    dtStart = null
                    dtEnd = null
                    url = null
                    desc = null
                }

                line == "END:VEVENT" -> {
                    if (inEvent && !summary.isBlank() && dtStart != null) {
                        val startMs = parseDate(dtStart!!)
                        val endMs = parseDate(dtEnd ?: dtStart!!)
                        result.add(
                            IcsEvent(
                                summary = summary,
                                location = location,
                                startMillis = startMs,
                                endMillis = endMs,
                                url = url,
                                description = desc
                            )
                        )
                    }
                    inEvent = false
                }

                inEvent && line.startsWith("SUMMARY:") -> {
                    summary = line.removePrefix("SUMMARY:")
                }

                inEvent && line.startsWith("LOCATION:") -> {
                    location = line.removePrefix("LOCATION:")
                }

                inEvent && line.startsWith("DTSTART") -> {
                    dtStart = line.substringAfter(':')
                }

                inEvent && line.startsWith("DTEND") -> {
                    dtEnd = line.substringAfter(':')
                }

                inEvent && line.startsWith("URL") -> {
                    url = line.substringAfter(':')
                }

                inEvent && line.startsWith("DESCRIPTION:") -> {
                    desc = line.removePrefix("DESCRIPTION:")
                }
            }
        }

        return result
    }

    private fun parseDate(raw: String): Long {
        // 可能是 20251207T120000 或 20251207
        val cleaned = raw.removeSuffix("Z")
        val format =
            if (cleaned.length == 8) SimpleDateFormat("yyyyMMdd", Locale.US)
            else SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US)

        // UW 在 Chicago 时区，这里用中部时区
        format.timeZone = TimeZone.getTimeZone("America/Chicago")
        val date: Date = format.parse(cleaned) ?: Date()
        return date.time
    }
}
