package com.remember.rememberme.feature.create_set.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QueryRequestBody(
    val model: String,
    val temperature: Float,
    val messages: List<QueryMessage>
) {
    companion object {
        private val promptPrefix = "Please come up with words and examples in English. Translate to: Russian. Use ^ symbol to separate the word, translation and example. Example must be always in English! Example: &&Body^ Тело^ I love my body, so I eat a lot of veggies^&& Theme: "
        private val promptPostfix = ". Level: Upper-intermidiate"
        fun generateFromQuery(query: String): QueryRequestBody {
            return QueryRequestBody(
                model = "gpt-3.5-turbo",
                temperature = 1f,
                messages = listOf(QueryMessage(content = "$promptPrefix$query$promptPostfix"))
            )
        }
    }
}
