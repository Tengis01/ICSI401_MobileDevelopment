package com.example.flashstudy.data

import java.util.UUID

// Neg flash card-iin medeelliig hadgalah data class
// leitnerBox: 1-5 hurtel, 1 = shine, 5 = ezemshsen
// needsReview: davtah shaardlagatai esehiig ilerhiilne
data class FlashCard(
    val id: String = UUID.randomUUID().toString(),
    val term: String,
    val definition: String,
    val leitnerBox: Int = 1,
    val needsReview: Boolean = true
)
