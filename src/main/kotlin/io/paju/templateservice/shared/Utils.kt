package io.paju.templateservice.shared

import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*

object DateFormat {
    val html = SimpleDateFormat("yyyy-MM-dd")
    val htmlDayMonth = SimpleDateFormat("dd.MM")
}

open class DateRange(val start: Date, val end: Date) : Iterable<Date>{
    override fun iterator(): Iterator<Date>  = object : Iterator<Date> {
        var current: Date = start

        override fun next(): Date {
            if (!hasNext()) {
                throw NoSuchElementException()
            }

            val result = current
            current = Date(current.toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli())
            return result
        }

        override fun hasNext(): Boolean {
            return current <= end
        }
    }
}