package com.taoufikcode.domain

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class EmailValidatorTest {

    @Test
    fun `accepts standard email addresses`() {
        listOf(
            "user@example.com",
            "first.last@example.com",
            "user+tag@example.co.uk",
            "user_name%test@sub.domain.org",
            "1234@numbers.io"
        ).forEach { email ->
            assertThat(EmailValidator.validate(email), name = email).isTrue()
        }
    }

    @Test
    fun `rejects malformed email addresses`() {
        listOf(
            "",
            "plainaddress",
            "@missinglocal.com",
            "user@",
            "user@domain",
            "user@domain.c",
            "user name@example.com",
            "user@exa mple.com"
        ).forEach { email ->
            assertThat(EmailValidator.validate(email), name = email).isFalse()
        }
    }
}
