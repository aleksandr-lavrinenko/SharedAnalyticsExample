package com.mobile.analytics

import com.mobile.analytics.event.Event

expect class Analytics: AnalyticsTracker {
    override fun trackEvent(event: Event)

    override fun setProfileID(profileID: String)
}