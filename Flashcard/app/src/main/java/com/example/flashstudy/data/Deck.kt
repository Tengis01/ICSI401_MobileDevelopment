package com.example.flashstudy.data

import java.util.UUID

// Neg bagts (deck) medeelel
// cards: ene bagtsad haralagdah buh kartuuruud
// lastStudied: suuld uzsen tsag (millisecond, epoch)
data class Deck(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val cards: List<FlashCard> = emptyList(),
    val lastStudied: Long = 0L,
    val isFolder: Boolean = false
)
