package com.chanomhub.myapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform