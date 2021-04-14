package com.mobile.analytics

import com.mobile.analytics.event.Event

interface AnalyticsTracker {
    fun trackEvent(event: Event)
    fun setProfileID(profileID: String) {}
}