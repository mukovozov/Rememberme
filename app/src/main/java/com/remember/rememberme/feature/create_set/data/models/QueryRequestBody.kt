package com.remember.rememberme.feature.create_set.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QueryRequestBody(
    val model: String,
    val temperature: Float,
    val messages: List<QueryMessage>
) {
    companion object {
        fun generateFromQuery(query: String): QueryRequestBody {
            return QueryRequestBody(
                model = "gpt-3.5-turbo",
                temperature = 1f,
                messages = listOf(QueryMessage(content = query))
            )
        }
    }
}
