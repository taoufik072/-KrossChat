package com.taoufikcode.chat.data.services

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.taoufikcode.chat.data.dto.ChatDto
import com.taoufikcode.chat.data.dto.ChatMessageDto
import com.taoufikcode.chat.data.dto.ChatParticipantDto
import com.taoufikcode.chat.data.dto.CreateChatDto
import com.taoufikcode.chat.data.respondJson
import com.taoufikcode.chat.data.testHttpClient
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ChatRemoteDataSourceTest {

    private val chatJson = """
        {
            "id": "chat-1",
            "participants": [
                {"userId": "u1", "username": "alice", "profilePictureUrl": null}
            ],
            "lastActivityAt": "2025-06-15T10:30:00Z",
            "lastMessage": null
        }
    """.trimIndent()

    private val expectedChatDto = ChatDto(
        id = "chat-1",
        participants = listOf(
            ChatParticipantDto(userId = "u1", username = "alice", profilePictureUrl = null)
        ),
        lastActivityAt = "2025-06-15T10:30:00Z",
        lastMessage = null
    )

    @Test
    fun `getChats parses the chat list on success`() = runTest {
        val engine = MockEngine { respondJson("[$chatJson]") }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.getChats()

        assertThat(result).isEqualTo(Result.Success(listOf(expectedChatDto)))
        val request = engine.requestHistory.single()
        assertThat(request.method).isEqualTo(HttpMethod.Get)
        assertThat(request.url.encodedPath).isEqualTo("/api/chat")
    }

    @Test
    fun `getChats maps server error to SERVER_ERROR`() = runTest {
        val engine = MockEngine {
            respondJson("", status = HttpStatusCode.InternalServerError)
        }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.getChats()

        assertThat(result).isEqualTo(Result.Failure(DataError.Remote.SERVER_ERROR))
    }

    @Test
    fun `getChatById requests the chat path`() = runTest {
        val engine = MockEngine { respondJson(chatJson) }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.getChatById("chat-1")

        assertThat(result).isEqualTo(Result.Success(expectedChatDto))
        assertThat(engine.requestHistory.single().url.encodedPath).isEqualTo("/api/chat/chat-1")
    }

    @Test
    fun `createChat posts the other user ids`() = runTest {
        val engine = MockEngine { respondJson(chatJson) }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.createChat(otherUserIds = listOf("u2", "u3"))

        assertThat(result).isEqualTo(Result.Success(expectedChatDto))
        val request = engine.requestHistory.single()
        assertThat(request.method).isEqualTo(HttpMethod.Post)
        assertThat(request.url.encodedPath).isEqualTo("/api/chat")
        val sentBody = Json.decodeFromString<CreateChatDto>(
            request.body.toByteArray().decodeToString()
        )
        assertThat(sentBody).isEqualTo(CreateChatDto(otherUserIds = listOf("u2", "u3")))
    }

    @Test
    fun `fetchMessages always sends pageSize and omits before when null`() = runTest {
        val engine = MockEngine { respondJson("[]") }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        dataSource.fetchMessages(chatId = "chat-1", before = null)

        val request = engine.requestHistory.single()
        assertThat(request.url.encodedPath).isEqualTo("/api/chat/chat-1/messages")
        assertThat(request.url.parameters["pageSize"])
            .isEqualTo(ChatRemoteDataSource.PAGE_SIZE.toString())
        assertThat(request.url.parameters["before"]).isNull()
    }

    @Test
    fun `fetchMessages sends before cursor when provided`() = runTest {
        val messageJson = """
            [{
                "id": "m1",
                "chatId": "chat-1",
                "content": "Hello",
                "createdAt": "2025-06-15T10:30:00Z",
                "senderId": "u1"
            }]
        """.trimIndent()
        val engine = MockEngine { respondJson(messageJson) }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.fetchMessages(chatId = "chat-1", before = "2025-06-15T00:00:00Z")

        assertThat(result).isEqualTo(
            Result.Success(
                listOf(
                    ChatMessageDto(
                        id = "m1",
                        chatId = "chat-1",
                        content = "Hello",
                        createdAt = "2025-06-15T10:30:00Z",
                        senderId = "u1"
                    )
                )
            )
        )
        assertThat(engine.requestHistory.single().url.parameters["before"])
            .isEqualTo("2025-06-15T00:00:00Z")
    }

    @Test
    fun `leaveChat deletes the leave path and returns empty result`() = runTest {
        val engine = MockEngine { respondJson("") }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.leaveChat("chat-1")

        assertThat(result).isEqualTo(Result.Success(Unit))
        val request = engine.requestHistory.single()
        assertThat(request.method).isEqualTo(HttpMethod.Delete)
        assertThat(request.url.encodedPath).isEqualTo("/api/chat/chat-1/leave")
    }

    @Test
    fun `searchParticipant sends the query parameter`() = runTest {
        val engine = MockEngine {
            respondJson("""{"userId": "u2", "username": "bob", "profilePictureUrl": null}""")
        }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.searchParticipant(query = "bob")

        assertThat(result).isEqualTo(
            Result.Success(
                ChatParticipantDto(userId = "u2", username = "bob", profilePictureUrl = null)
            )
        )
        val request = engine.requestHistory.single()
        assertThat(request.url.encodedPath).isEqualTo("/api/participants")
        assertThat(request.url.parameters["query"]).isEqualTo("bob")
    }

    @Test
    fun `deleteMessage deletes the message path`() = runTest {
        val engine = MockEngine { respondJson("") }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.deleteMessage("m1")

        assertThat(result).isEqualTo(Result.Success(Unit))
        val request = engine.requestHistory.single()
        assertThat(request.method).isEqualTo(HttpMethod.Delete)
        assertThat(request.url.encodedPath).isEqualTo("/api/messages/m1")
    }

    @Test
    fun `searchParticipant maps 404 to NOT_FOUND`() = runTest {
        val engine = MockEngine { respondJson("", status = HttpStatusCode.NotFound) }
        val dataSource = ChatRemoteDataSource(testHttpClient(engine))

        val result = dataSource.searchParticipant(query = "nobody")

        assertThat(result).isEqualTo(Result.Failure(DataError.Remote.NOT_FOUND))
    }
}
