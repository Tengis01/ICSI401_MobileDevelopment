package com.example.flashstudy.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface DeckDao {
    @Transaction
    @Query("SELECT * FROM decks")
    fun getDecksWithCards(): List<DeckWithCards>

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckWithCardsById(deckId: String): DeckWithCards?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDeck(deck: DeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCards(cards: List<FlashCardEntity>)

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    fun deleteCardsByDeckId(deckId: String)

    @Delete
    fun deleteDeck(deck: DeckEntity)

    @Update
    fun updateCard(card: FlashCardEntity)
    
    @Update
    fun updateDeck(deck: DeckEntity)

    @Transaction
    fun saveDeckWithCards(deck: DeckEntity, cards: List<FlashCardEntity>) {
        insertDeck(deck)
        deleteCardsByDeckId(deck.id)
        insertCards(cards)
    }

    // --- Folder Operations ---

    @Transaction
    @Query("SELECT * FROM folders")
    fun getFoldersWithDecks(): List<FolderWithDecks>

    @Transaction
    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderWithDecksById(folderId: String): FolderWithDecks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFolder(folder: FolderEntity)

    @Delete
    fun deleteFolder(folder: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFolderDeckCrossRef(crossRef: FolderDeckCrossRef)

    @Query("DELETE FROM folder_deck_cross_ref WHERE folderId = :folderId AND deckId = :deckId")
    fun deleteFolderDeckCrossRef(folderId: String, deckId: String)
}
