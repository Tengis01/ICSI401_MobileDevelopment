package com.example.flashstudy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Embedded
import androidx.room.Relation
import com.example.flashstudy.data.Deck
import com.example.flashstudy.data.FlashCard

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val lastStudied: Long,
    val isFolder: Boolean
) {
    fun toDeck(cards: List<FlashCardEntity>): Deck {
        return Deck(
            id = id,
            name = name,
            description = description,
            cards = cards.map { it.toFlashCard() },
            lastStudied = lastStudied,
            isFolder = isFolder
        )
    }
}

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"])]
)
data class FlashCardEntity(
    @PrimaryKey val id: String,
    val deckId: String,
    val term: String,
    val definition: String,
    val leitnerBox: Int,
    val needsReview: Boolean
) {
    fun toFlashCard(): FlashCard {
        return FlashCard(
            id = id,
            term = term,
            definition = definition,
            leitnerBox = leitnerBox,
            needsReview = needsReview
        )
    }
}

data class DeckWithCards(
    @Embedded val deck: DeckEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<FlashCardEntity>
) {
    fun toDeck(): Deck = deck.toDeck(cards)
}

fun Deck.toDeckEntity(): DeckEntity = DeckEntity(
    id = id,
    name = name,
    description = description,
    lastStudied = lastStudied,
    isFolder = isFolder
)

fun FlashCard.toFlashCardEntity(deckId: String): FlashCardEntity = FlashCardEntity(
    id = id,
    deckId = deckId,
    term = term,
    definition = definition,
    leitnerBox = leitnerBox,
    needsReview = needsReview
)

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val createdAt: Long
) {
    fun toFolder(deckIds: List<String>): com.example.flashstudy.data.Folder {
        return com.example.flashstudy.data.Folder(
            id = id,
            name = name,
            description = description,
            deckIds = deckIds,
            createdAt = createdAt
        )
    }
}

@Entity(
    tableName = "folder_deck_cross_ref",
    primaryKeys = ["folderId", "deckId"],
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"])]
)
data class FolderDeckCrossRef(
    val folderId: String,
    val deckId: String
)

data class FolderWithDecks(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = androidx.room.Junction(
            value = FolderDeckCrossRef::class,
            parentColumn = "folderId",
            entityColumn = "deckId"
        )
    )
    val decks: List<DeckEntity>
) {
    fun toFolder(): com.example.flashstudy.data.Folder {
        return folder.toFolder(decks.map { it.id })
    }
}

fun com.example.flashstudy.data.Folder.toFolderEntity(): FolderEntity = FolderEntity(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt
)
