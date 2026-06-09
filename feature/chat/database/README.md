# KrossChat — Database Schema

![Database Schema](schema.svg)

## Entities

| Table | Columns |
|---|---|
| `ChatEntity` | `chatId` PK · `lastActivityAt` |
| `ParticipantEntity` | `userId` PK · `username` · `profilePictureUrl` |
| `MessageEntity` | `messageId` PK · `chatId` FK · `senderId` FK · `content` · `timestamp` · `deliveryStatus` · `deliveryStatusTimestamp` |
| `ChatParticipantJoin` | `chatId` PK+FK · `userId` PK+FK · `isActive` |

## Views

| View | Definition |
|---|---|
| `last_message_view_per_chat` | Most recent `MessageEntity` per `chatId` (by `MAX(timestamp)`) |

## Relationships

| From | To | Cardinality | Detail |
|---|---|---|---|
| `ChatEntity` | `MessageEntity` | one-to-many | `chatId` FK, cascade delete |
| `ChatEntity` | `ParticipantEntity` | many-to-many | via `ChatParticipantJoin` junction |
| `ParticipantEntity` | `MessageEntity` | one-to-many | `senderId` references `userId` |
| `ChatEntity` | `last_message_view_per_chat` | one-to-zero-or-one | DB view, most recent message per chat |

## DAOs

### ChatDao

| Method | Operation |
|---|---|
| `upsertChat(chat)` | Upsert single chat |
| `upsertChats(chats)` | Upsert list of chats |
| `deleteChatById(chatId)` | Delete by ID |
| `deleteAllChats()` | Delete all chats |
| `deleteChatsByIds(ids)` | Batch delete |
| `getAllChatIds()` | All chat IDs |
| `getChatCount()` | Count (Flow) |
| `getChatById(chatId)` | Chat + participants + last message |
| `getChatsWithParticipants()` | All chats ordered by activity (Flow) |
| `getActiveParticipantsByChatId(chatId)` | Active participants for a chat (Flow) |
| `getChatInfoById(chatId)` | Chat + all messages with senders (Flow) |
| `upsertChatWithParticipantsAndCrossRefs(...)` | Transaction: upsert chat, participants, join rows |
| `upsertChatsWithParticipantsAndCrossRefs(...)` | Transaction: bulk sync chats, prune stale entries |

### ParticipantDao

| Method | Operation |
|---|---|
| `upsertParticipant(participant)` | Upsert single participant |
| `upsertParticipants(participants)` | Upsert list |
| `getAllParticipants()` | All participants |

### MessageDao

| Method | Operation |
|---|---|
| `upsertMessage(message)` | Upsert single message |
| `upsertMessages(messages)` | Upsert list |
| `deleteMessageById(messageId)` | Delete by ID |
| `deleteMessageById(messageIds)` | Batch delete by IDs |
| `getMessagesByChatId(chatId)` | Messages for a chat, newest first (Flow) |
| `getMessageById(messageId)` | Single message |

### ChatParticipantsJoinDao

| Method | Operation |
|---|---|
| `upsertCrossRefs(crossRefs)` | Upsert join rows |
| `getActiveParticipantIdsByChat(chatId)` | Active participant IDs for a chat |
| `getAllParticipantIdsByChat(chatId)` | All (active + inactive) participant IDs |
| `markParticipantsAsInactive(chatId, userIds)` | Soft-remove participants from a chat |
| `reactivateParticipants(chatId, userIds)` | Restore previously removed participants |
| `syncChatParticipants(chatId, participants)` | Transaction: reactivate/deactivate/add participants |
