@file:OptIn(ExperimentalCoroutinesApi::class)

package com.taoufikcode.presentation.register

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.Result
import com.taoufikcode.core.presentation.utils.UiText
import com.taoufikcode.presentation.FakeAuthService
import com.taoufikcode.presentation.awaitUntil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import krosschat.feature.auth.presentation.generated.resources.Res
import krosschat.feature.auth.presentation.generated.resources.error_account_exists
import krosschat.feature.auth.presentation.generated.resources.error_invalid_email
import krosschat.feature.auth.presentation.generated.resources.error_invalid_password
import krosschat.feature.auth.presentation.generated.resources.error_invalid_username
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class RegisterViewModelTest {

    private lateinit var authService: FakeAuthService
    private lateinit var viewModel: RegisterViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        authService = FakeAuthService()
        viewModel = RegisterViewModel(authService)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun typeForm(email: String, username: String, password: String) {
        viewModel.state.value.emailTextState.setTextAndPlaceCursorAtEnd(email)
        viewModel.state.value.usernameTextState.setTextAndPlaceCursorAtEnd(username)
        viewModel.state.value.passwordTextState.setTextAndPlaceCursorAtEnd(password)
    }

    @Test
    fun `register with invalid inputs sets field errors and skips the service call`() = runTest {
        viewModel.state.test {
            awaitItem()
            typeForm(email = "invalid", username = "ab", password = "weak")

            viewModel.onAction(RegisterAction.OnRegisterClick)

            val errorState = awaitUntil {
                it.emailError != null && it.usernameError != null && it.passwordError != null
            }
            assertThat((errorState.emailError as UiText.Resource).id)
                .isEqualTo(Res.string.error_invalid_email)
            assertThat((errorState.usernameError as UiText.Resource).id)
                .isEqualTo(Res.string.error_invalid_username)
            assertThat((errorState.passwordError as UiText.Resource).id)
                .isEqualTo(Res.string.error_invalid_password)
            assertThat(authService.registerCalls).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `only the invalid field gets an error`() = runTest {
        viewModel.state.test {
            awaitItem()
            typeForm(email = "user@example.com", username = "kross", password = "weak")

            viewModel.onAction(RegisterAction.OnRegisterClick)

            val errorState = awaitUntil { it.passwordError != null }
            assertThat(errorState.emailError).isNull()
            assertThat(errorState.usernameError).isNull()
            assertThat(authService.registerCalls).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `successful register emits Success event with the email`() = runTest {
        viewModel.state.test {
            awaitItem()
            typeForm(email = "new@example.com", username = "newuser", password = "Secret123")

            viewModel.events.test {
                viewModel.onAction(RegisterAction.OnRegisterClick)

                assertThat(awaitItem()).isEqualTo(RegisterEvent.Success(email = "new@example.com"))
            }

            assertThat(authService.registerCalls)
                .containsExactly(Triple("new@example.com", "newuser", "Secret123"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register conflict shows account exists error`() = runTest {
        authService.registerResult = Result.Failure(DataError.Remote.CONFLICT)

        viewModel.state.test {
            awaitItem()
            typeForm(email = "taken@example.com", username = "taken", password = "Secret123")

            viewModel.onAction(RegisterAction.OnRegisterClick)

            val errorState = awaitUntil { it.registrationError != null }
            assertThat((errorState.registrationError as UiText.Resource).id)
                .isEqualTo(Res.string.error_account_exists)
            assertThat(errorState.isRegistering).isEqualTo(false)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
