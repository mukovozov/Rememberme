package com.remember.rememberme.feature.create_set.data

import com.remember.rememberme.BuildConfig
import com.remember.rememberme.core.coroutines.Outcome
import com.remember.rememberme.feature.create_set.data.models.QueryMessage
import com.remember.rememberme.feature.create_set.data.models.QueryRequestBody
import com.remember.rememberme.feature.create_set.data.models.QueryRequestBodyJsonAdapter
import com.remember.rememberme.feature.create_set.data.models.QueryResponse
import com.remember.rememberme.feature.create_set.data.models.QueryResponseJsonAdapter
import com.squareup.moshi.Moshi
import fuel.HttpLoader
import fuel.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SetCreationRepository {
    suspend fun createFromQuery(query: String): Outcome<QueryMessage>
}

class MockSetCreationRepositoryImpl @Inject constructor(

) : SetCreationRepository {
    override suspend fun createFromQuery(query: String): Outcome<QueryMessage> {
        val example1 = "&&Finna^ Собираюсь^ I'm finna go to the store to grab some snacks.^&&\n&&Savage^ Брутален^ That comeback was savage!^&&\n&&Lit^ Клевый^ The party last night was so lit!^&&\n&&Flex^ Хвастаться^ Look at him flexing his new car.^&&\n&&FOMO^ Страх пропустить что-то интересное^ I have major FOMO right now; everyone's at the concert and I couldn't get tickets.^&&\n&&Glow up^ Трансформация в лучшую сторону^ Did you see her glow up? She looks amazing now!^&&\n&&Sip tea^ Передавать сплетни^ She loves to sip tea and gossip about others.^&&\n&&Yas^ Ура!^ Yas! I finally got the promotion I've been working so hard for.^&&\n&&G.O.A.T^ Лучший из лучших^ LeBron James is considered the G.O.A.T in basketball.^&&\n&&Woke^ Прозрел^ After attending that social justice seminar, I feel woke about the important issues in our society.^&&"
        val example = """&&Flex. Показывать. I'm gonna flexz my new shoes on TikTok.&&\n&&Savage. Беспощадный. That dance routine was so savage! I couldn't keep up.&&\n&&Lit. Крутой. The party last night was lit! The music was amazing.&&\n&&Glow up. Преобразование. Did you see her glow up? She looks completely different now.&&\n&&Clout. Влияние. He's always trying to gain clout on social media. It's all about followers for him.&&\n&&Tea. Скрытая информация. spill the tea - расскажи пикантности. I need to know all the tea on what happened last night.&&\n&&Vibing. Отдыхать с хорошим настроением. Just vibing with my friends, listening to some good music.&&\n&&FOMO. Страх пропустить. I couldn't miss the party; I had such bad FOMO.&&\n&&Woke. Пробужденный. She's so woke, always fighting for social justice.&&\n&&Clap back. Противостоять. She had the best clap back to that mean comment.&&"""
        return Outcome.Success(QueryMessage(content = example1))
    }
}

class SetCreationRepositoryImpl @Inject constructor(
    private val loader: HttpLoader,
    private val moshi: Moshi,
) : SetCreationRepository {
    override suspend fun createFromQuery(query: String): Outcome<QueryMessage> {
        return withContext(Dispatchers.IO) {
            val queryRequestBody = QueryRequestBody.generateFromQuery(query)
            val requestAdapter = QueryRequestBodyJsonAdapter(moshi)

            val body = requestAdapter.toJson(queryRequestBody)
            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .headers(mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer ${BuildConfig.CHAT_GPT_TOKEN}")
                )
                .body(body)
                .build()

            val responseString = loader.post(request)

            if (responseString.statusCode != 200) {
                return@withContext Outcome.Error(RuntimeException("Server error"))
            }

            return@withContext try {
                val responseAdapter = QueryResponseJsonAdapter(moshi)
                val response = responseAdapter.fromJson(responseString.body)
                    ?.choices
                    ?.firstOrNull()
                    ?.message
                    ?: error("Failed to parse")

                Outcome.Success(response)
            } catch (e: Exception) {
                Outcome.Error(e)
            }
        }
    }
}