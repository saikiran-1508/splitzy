package com.example.splitzy.domain.model

// The one thing that varies by context (a weekend trip, a shared apartment,
// tracking your own or your family's spending) is how a group is labeled —
// the underlying "members split expenses" mechanics stay identical.
enum class GroupType {
    TRIP,
    HOUSEHOLD,
    PERSONAL_FAMILY
}
