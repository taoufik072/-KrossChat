package com.taoufikcode.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import com.taoufikcode.core.domain.auth.SessionStorage

class ChatListViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel()