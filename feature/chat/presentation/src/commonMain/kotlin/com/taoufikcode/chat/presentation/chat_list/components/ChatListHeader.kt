package com.taoufikcode.chat.presentation.chat_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taoufikcode.chat.presentation.components.ChatHeader
import com.taoufikcode.core.designsystem.components.avatar.ChatParticipantUi
import com.taoufikcode.core.designsystem.components.avatar.KrossAvatarPhoto
import com.taoufikcode.core.designsystem.components.brand.KrossHorizontalDivider
import com.taoufikcode.core.designsystem.components.dropdown.DropDownItem
import com.taoufikcode.core.designsystem.components.dropdown.KrossDropDownMenu
import com.taoufikcode.core.designsystem.theme.KrossChatTheme
import com.taoufikcode.core.designsystem.theme.extended
import krosschat.core.designsystem.generated.resources.log_out_icon
import krosschat.core.designsystem.generated.resources.logo_kross
import krosschat.core.designsystem.generated.resources.users_icon
import krosschat.feature.chat.presentation.generated.resources.Res
import krosschat.feature.chat.presentation.generated.resources.logout
import krosschat.feature.chat.presentation.generated.resources.profile_settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import krosschat.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun ChatListHeader(
    currentUser: ChatParticipantUi?,
    isUserMenuOpen: Boolean,
    onUserAvatarClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatHeader(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = vectorResource(DesignSystemRes.drawable.logo_kross),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Kross",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            ProfileAvatarSection(
                currentUser = currentUser,
                isMenuOpen = isUserMenuOpen,
                onClick = onUserAvatarClick,
                onDismissMenu = onDismissMenu,
                onProfileSettingsClick = onProfileSettingsClick,
                onLogoutClick = onLogoutClick,
            )
        }
    }
}

@Composable
fun ProfileAvatarSection(
    currentUser: ChatParticipantUi?,
    isMenuOpen: Boolean,
    onClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        if (currentUser != null) {
            KrossAvatarPhoto(
                displayText = currentUser.initials,
                imageUrl = currentUser.imageUrl,
                onClick = onClick
            )
        }

        KrossDropDownMenu(
            isOpen = isMenuOpen,
            onDismiss = onDismissMenu,
            items = listOf(
                DropDownItem(
                    title = stringResource(Res.string.profile_settings),
                    icon = vectorResource(DesignSystemRes.drawable.users_icon),
                    contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                    onClick = onProfileSettingsClick
                ),
                DropDownItem(
                    title = stringResource(Res.string.logout),
                    icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                    contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                    onClick = onLogoutClick
                ),
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ChatListHeaderPreview() {
    KrossChatTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ChatListHeader(
                currentUser = ChatParticipantUi(
                    id = "1",
                    username = "Taoufik",
                    initials = "TA",
                ),
                isUserMenuOpen = true,
                onUserAvatarClick = {},
                onDismissMenu = {},
                onProfileSettingsClick = {},
                onLogoutClick = {}
            )
        }
    }
}