package com.taoufikcode.presentation.di

import com.taoufikcode.presentation.email_verification.EmailVerificationViewModel
import com.taoufikcode.presentation.login.LoginViewModel
import com.taoufikcode.presentation.register.RegisterViewModel
import com.taoufikcode.presentation.register_success.RegisterSuccessViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
     viewModelOf(::RegisterViewModel)
     viewModelOf(::RegisterSuccessViewModel)
     viewModelOf(::EmailVerificationViewModel)
     viewModelOf(::LoginViewModel)

}