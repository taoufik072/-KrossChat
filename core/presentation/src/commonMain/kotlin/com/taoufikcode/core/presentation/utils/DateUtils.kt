package com.taoufikcode.core.presentation.utils

import com.taoufikcode.core.presentation.utils.UiText.Resource
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import krosschat.core.presentation.generated.resources.Res
import krosschat.core.presentation.generated.resources.today
import krosschat.core.presentation.generated.resources.today_x
import krosschat.core.presentation.generated.resources.yesterday
import krosschat.core.presentation.generated.resources.yesterday_x
import kotlin.time.Clock
import kotlin.time.Instant

object DateUtils {

    fun formatMessageTime(instant: Instant, clock: Clock = Clock.System): UiText {
        val timeZone = TimeZone.currentSystemDefault()
        val messageDateTime = instant.toLocalDateTime(timeZone)
        val todayDate = clock.now().toLocalDateTime(timeZone).date
        val yesterdayDate = todayDate.minus(1, DateTimeUnit.DAY)

        val formattedTime = messageDateTime.format(
            format = LocalDateTime.Format {
                amPmHour()
                char(':')
                minute()
                amPmMarker("am", "pm")
            }
        )
        val formattedDateTime = messageDateTime.format(
            LocalDateTime.Format {
                day()
                char('/')
                monthNumber()
                char('/')
                year()
                chars(", $formattedTime")
            }
        )

        return when (messageDateTime.date) {
            todayDate -> Resource(Res.string.today_x, arrayOf(formattedTime))
            yesterdayDate -> Resource(Res.string.yesterday_x, arrayOf(formattedTime))
            else -> UiText.DynamicString(formattedDateTime)
        }
    }

    fun formatDateSeparator(date: LocalDate, clock: Clock = Clock.System): UiText {
        val timeZone = TimeZone.currentSystemDefault()
        val today = clock.now().toLocalDateTime(timeZone).date
        val yesterday = today.minus(1, DateTimeUnit.DAY)

        return when (date) {
            today -> Resource(Res.string.today)
            yesterday -> Resource(Res.string.yesterday)
            else -> {
                val formatted = date.format(
                    LocalDate.Format {
                        day()
                        char('/')
                        monthNumber()
                        char('/')
                        year()
                    }
                )
                UiText.DynamicString(formatted)
            }
        }
    }
}