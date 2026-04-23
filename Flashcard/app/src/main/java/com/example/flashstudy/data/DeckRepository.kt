package com.example.flashstudy.data

import com.example.flashstudy.data.local.DeckDao
import com.example.flashstudy.data.local.toDeckEntity
import com.example.flashstudy.data.local.toFlashCardEntity
import com.example.flashstudy.data.local.toFolderEntity

class DeckRepository(private val deckDao: DeckDao) {

    fun getDecks(): List<Deck> {
        return deckDao.getDecksWithCards().map { it.toDeck() }
    }

    fun saveDeck(deck: Deck) {
        val deckEntity = deck.toDeckEntity()
        val cardEntities = deck.cards.map { it.toFlashCardEntity(deck.id) }
        deckDao.saveDeckWithCards(deckEntity, cardEntities)
    }

    fun deleteDeck(deckId: String) {
        val deckEntity = deckDao.getDeckWithCardsById(deckId)?.deck
        if (deckEntity != null) {
            deckDao.deleteDeck(deckEntity)
        }
    }

    fun getDeckById(deckId: String): Deck? {
        return deckDao.getDeckWithCardsById(deckId)?.toDeck()
    }

    fun updateCard(deckId: String, updatedCard: FlashCard) {
        val cardEntity = updatedCard.toFlashCardEntity(deckId)
        deckDao.updateCard(cardEntity)
    }

    // --- Folder Methods ---

    fun getFolders(): List<Folder> {
        return deckDao.getFoldersWithDecks().map { it.toFolder() }
    }

    fun saveFolder(folder: Folder) {
        deckDao.insertFolder(folder.toFolderEntity())
        // Keep cross refs intact - we just update the folder itself.
        // The user's requested Folder data class has deckIds, but we manage them via addDeckToFolder.
        // Wait, if folder has deckIds on save, we might need to sync them.
        // Let's implement full sync if deckIds is provided.
        val currentDecks = getFolderById(folder.id)?.deckIds ?: emptyList()
        val newDecks = folder.deckIds
        
        val toRemove = currentDecks - newDecks.toSet()
        val toAdd = newDecks - currentDecks.toSet()

        toRemove.forEach { deckId -> deckDao.deleteFolderDeckCrossRef(folder.id, deckId) }
        toAdd.forEach { deckId -> deckDao.insertFolderDeckCrossRef(com.example.flashstudy.data.local.FolderDeckCrossRef(folder.id, deckId)) }
    }

    fun deleteFolder(folderId: String) {
        val folderEntity = deckDao.getFolderWithDecksById(folderId)?.folder
        if (folderEntity != null) {
            deckDao.deleteFolder(folderEntity)
        }
    }

    fun getFolderById(folderId: String): Folder? {
        return deckDao.getFolderWithDecksById(folderId)?.toFolder()
    }

    fun addDeckToFolder(folderId: String, deckId: String) {
        deckDao.insertFolderDeckCrossRef(com.example.flashstudy.data.local.FolderDeckCrossRef(folderId, deckId))
    }

    fun removeDeckFromFolder(folderId: String, deckId: String) {
        deckDao.deleteFolderDeckCrossRef(folderId, deckId)
    }
}
