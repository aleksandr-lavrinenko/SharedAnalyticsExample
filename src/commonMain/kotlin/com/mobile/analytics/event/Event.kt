package com.mobile.analytics.event

typealias EventName = String

abstract class Event {
    abstract val id: EventId
    abstract val params: Map<String, Any>
    val name: EventName = classNameToEventName()
}

private fun Event.classNameToEventName(): EventName {
    val className = this::class.qualifiedName!!
    val pkgName = "com.mobile.analytics.event"
    return className
        .subSequence(pkgName.length + 1, className.length - "Event".length)
        .mapIndexed { index, ch ->
            when (ch) {
                '.' -> "_"
                in 'A'..'Z' -> if (index == 0) "$ch" else "_$ch"
                else -> "${ch.toUpperCase()}"
            }
        }
        .fold(StringBuilder()) { acc, next -> acc.append(next) }
        .toString()
}

fun Event.id(value: Int) = EventId(value)
