package com.taoufikcode.core.domain.validation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class PasswordValidatorTest {

    @Test
    fun `valid password passes all rules`() {
        val state = PasswordValidator.validate("Abcdefgh1")

        assertThat(state).isEqualTo(
            PasswordValidationState(
                hasMinLength = true,
                hasDigit = true,
                hasUppercase = true
            )
        )
        assertThat(state.isValidPassword).isTrue()
    }

    @Test
    fun `password shorter than 9 characters fails min length`() {
        val state = PasswordValidator.validate("Abcdef1")

        assertThat(state.hasMinLength).isFalse()
        assertThat(state.hasDigit).isTrue()
        assertThat(state.hasUppercase).isTrue()
        assertThat(state.isValidPassword).isFalse()
    }

    @Test
    fun `password of exactly 9 characters passes min length`() {
        val state = PasswordValidator.validate("Abcdefg12")

        assertThat(state.hasMinLength).isTrue()
        assertThat(state.isValidPassword).isTrue()
    }

    @Test
    fun `password without digit fails digit rule`() {
        val state = PasswordValidator.validate("Abcdefghij")

        assertThat(state.hasDigit).isFalse()
        assertThat(state.isValidPassword).isFalse()
    }

    @Test
    fun `password without uppercase fails uppercase rule`() {
        val state = PasswordValidator.validate("abcdefgh1")

        assertThat(state.hasUppercase).isFalse()
        assertThat(state.isValidPassword).isFalse()
    }

    @Test
    fun `empty password fails all rules`() {
        val state = PasswordValidator.validate("")

        assertThat(state).isEqualTo(
            PasswordValidationState(
                hasMinLength = false,
                hasDigit = false,
                hasUppercase = false
            )
        )
        assertThat(state.isValidPassword).isFalse()
    }
}
