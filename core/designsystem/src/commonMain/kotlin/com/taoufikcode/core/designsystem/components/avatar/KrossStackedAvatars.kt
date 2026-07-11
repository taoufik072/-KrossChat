package com.taoufikcode.core.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KrossStackedAvatars(
    avatars: List<ChatParticipantUi>,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    maxVisible: Int = 2,
    overlapPercentage: Float = 0.4f
) {
    val overlapOffset = -(size.dp * overlapPercentage)

    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = (avatars.size - maxVisible).coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatars.forEach { avatarUi ->
            KrossAvatarPhoto(
                displayText = avatarUi.initials,
                size = size,
                imageUrl = avatarUi.imageUrl
            )
        }

        if(remainingCount > 0) {
            KrossAvatarPhoto(
                displayText = "$remainingCount+",
                size = size,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview
fun KrossStackedAvatarsPreview() {
    KrossChatTheme {
        KrossStackedAvatars(
            avatars = listOf(
                ChatParticipantUi(
                    id = "1",
                    username = "Taoufik",
                    initials = "TB",
                ),
                ChatParticipantUi(
                    id = "2",
                    username = "Gabr",
                    initials = "GB",
                ),
                ChatParticipantUi(
                    id = "3",
                    username = "Isso",
                    initials = "IS",
                ),
                ChatParticipantUi(
                    id = "4",
                    username = "Taoufik",
                    initials = "TB",
                ),


            )
        )
    }
}