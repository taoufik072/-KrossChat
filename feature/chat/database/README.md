# KrossChat — Database Schema

![Database Schema](schema.svg)

## Relationships

| From | To | Cardinality | Detail |
|---|---|---|---|
| `ChatEntity` | `ChatMessageEntity` | one-to-many | `chatId` FK, cascade delete |
| `ChatEntity` | `ChatParticipantEntity` | many-to-many | via `ChatParticipantCrossRef` junction |
| `ChatParticipantEntity` | `ChatMessageEntity` | one-to-many | `senderId` references `userId` |
| `ChatEntity` | `last_message_view_per_chat` | one-to-zero-or-one | DB view, most recent message per chat |
