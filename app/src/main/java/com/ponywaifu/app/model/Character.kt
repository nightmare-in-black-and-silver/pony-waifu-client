package com.ponywaifu.app.model

data class Character(
    val id: String,
    val name: String,
    val summary: String,
    val profilePicturePath: String? = null,
    val neutralExpressionPath: String? = null,
    val available: Boolean = true
)
