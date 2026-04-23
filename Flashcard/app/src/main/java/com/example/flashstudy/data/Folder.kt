package com.example.flashstudy.data

import java.util.UUID

data class Folder(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val deckIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
