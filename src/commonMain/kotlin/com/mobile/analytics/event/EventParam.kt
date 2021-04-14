package com.mobile.analytics.event

abstract class EventParam<T> {
    val name: String = classNameToEventParamName()
    abstract val value: T
}

private fun EventParam<*>.classNameToEventParamName(): String {
    val className = this::class.simpleName!!
    return className
        .subSequence(0, className.length - "Param".length)
        .mapIndexed { index, ch ->
            when (ch) {
                in 'A'..'Z' -> if (index == 0) "${ch.toLowerCase()}" else "_${ch.toLowerCase()}"
                else -> "$ch"
            }
        }
        .fold(StringBuilder()) { acc, next -> acc.append(next) }
        .toString()
}

internal fun Array<out EventParam<*>>.toMap(): Map<String, Any> =
    fold(mutableMapOf()) { acc, param ->
        acc[param.name] = param.value!!
        acc
    }

abstract class LongParam(override val value: Long) : EventParam<Long>()
abstract class StringParam(override val value: String) : EventParam<String>()
