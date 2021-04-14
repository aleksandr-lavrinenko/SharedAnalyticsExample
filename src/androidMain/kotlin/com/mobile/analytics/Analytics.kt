package com.mobile.analytics

import com.mobile.analytics.event.Event

actual class Analytics private constructor(private val trackers: List<AnalyticsTracker>): AnalyticsTracker {
    actual override fun trackEvent(event: Event) {
        trackers.forEach { it.trackEvent(event) }
    }

    actual override fun setProfileID(profileID: String) {
        trackers.forEach { it.setProfileID(profileID) }
    }

    companion object {
        operator fun invoke(trackers: List<AnalyticsTracker>): Analytics {
            return Analytics(trackers)
        }
    }
}