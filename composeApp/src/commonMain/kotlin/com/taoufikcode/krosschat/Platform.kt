package com.taoufikcode.krosschat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform