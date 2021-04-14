package com.mobile.analytics

import com.mobile.analytics.event.Event
import kotlin.native.concurrent.freeze

actual class Analytics constructor(private val trackers: List<AnalyticsTracker>): AnalyticsTracker {
    actual override fun trackEvent(event: Event) {
        trackers.forEach { it.trackEvent(event) }
    }

    actual override fun setProfileID(profileID: String) {
        trackers.forEach { it.setProfileID(profileID) }
    }

    companion object Factory {
        operator fun invoke(trackers: List<AnalyticsTracker>): Analytics {
            val analytics = Analytics(trackers)
            analytics.freeze()
            return analytics
        }
    }
}