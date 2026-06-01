package com.taoufikcode.core.presentation

expect fun platform(): String

val isIos: Boolean get() = platform() == "iOS"