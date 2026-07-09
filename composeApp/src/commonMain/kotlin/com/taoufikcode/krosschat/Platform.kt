package com.taoufikcode.krosschat

interface Platform {
    val name: String
    val os: String
}

expect fun getPlatform(): Platform