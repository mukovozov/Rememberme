package com.remember.rememberme.feature.create_set.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QueryMessage(
    val role: String = "user",
    val content: String
)