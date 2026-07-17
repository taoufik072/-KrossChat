@file:OptIn(ExperimentalCoroutinesApi::class)

package com.taoufikcode.presentation.login

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshots.Snapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.presentation.utils.UiText
import com.taoufikcode.presentation.FakeAuthService
import com.taoufikcode.presentation.FakeSessionStorage
import com.taoufikcode.presentation.awaitUntil
import com.taoufikcode.presentation.testAuthInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import krosschat.feature.auth.presentation.generated.resources.Res
import krosschat.feature.auth.presentation.generated.resources.error_email_not_verified
import krosschat.feature.auth.presentation.generated.resources.error_invalid_credentials
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LoginViewModelTest {

    private lateinit var authService: FakeAuthService
    private lateinit var sessionStorage: FakeSessionStorage
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        authService = FakeAuthService()
        sessionStorage = FakeSessionStorage()
        viewModel = LoginViewModel(authService, sessionStorage)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun typeCredentials(email: String, password: String) {
        viewModel.state.value.emailTextFieldState.setTextAndPlaceCursorAtEnd(email)
        viewModel.state.value.passwordTextFieldState.setTextAndPlaceCursorAtEnd(password)
        Snapshot.sendApplyNotifications()
    }

    @Test
    fun `canLogin becomes true for valid email and non-blank password`() = runTest {
        viewModel.state.test {
            awaitItem()
            typeCredentials(email = "user@example.com", password = "Secret123")

            assertThat(awaitUntil { it.canLogin }.canLogin).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login click is ignored while canLogin is false`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(LoginAction.OnLoginClick)

            assertThat(authService.loginCalls).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `successful login stores session and emits Success event`() = runTest {
        viewModel.state.test {
            awaitItem()
            typeCredentials(email = "user@example.com", password = "Secret123")
            awaitUntil { it.canLogin }

            viewModel.events.test {
                viewModel.onAction(LoginAction.OnLoginClick)

                assertThat(awaitItem()).isEqualTo(LoginEvent.Success)
            }

            assertThat(authService.loginCalls)
                .containsExactly("user@example.com" to "Secret123")
            assertThat(sessionStorage.storedAuthInfo).isEqualTo(testAuthInfo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unauthorized login shows invalid credentials error`() = runTest {
        authService.loginResult = Result.Failure(DataError.Remote.UNAUTHORIZED)

        viewModel.state.test {
            awaitItem()
            typeCredentials(email = "user@example.com", password = "WrongPass1")
            awaitUntil { it.canLogin }

            viewModel.onAction(LoginAction.OnLoginClick)

            val errorState = awaitUntil { it.error != null }
            assertThat((errorState.error as UiText.Resource).id)
                .isEqualTo(Res.string.error_invalid_credentials)
            assertThat(errorState.isLoggingIn).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `forbidden login shows email not verified error`() = runTest {
        authService.loginResult = Result.Failure(DataError.Remote.FORBIDDEN)

        viewModel.state.test {
            awaitItem()
            typeCredentials(email = "user@example.com", password = "Secret123")
            awaitUntil { it.canLogin }

            viewModel.onAction(LoginAction.OnLoginClick)

            val errorState = awaitUntil { it.error != null }
            assertThat((errorState.error as UiText.Resource).id)
                .isEqualTo(Res.string.error_email_not_verified)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggling password visibility flips the flag`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(LoginAction.OnTogglePasswordVisibility)

            assertThat(awaitUntil { it.isPasswordVisible }.isPasswordVisible).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
