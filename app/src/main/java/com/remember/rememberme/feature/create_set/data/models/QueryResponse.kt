package com.remember.rememberme.feature.create_set.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QueryResponse(
    val choices: List<QueryChoiceResponse>
)

@JsonClass(generateAdapter = true)
data class QueryChoiceResponse(
    val message: QueryMessage
)