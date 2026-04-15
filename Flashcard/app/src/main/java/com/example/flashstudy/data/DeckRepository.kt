package com.example.flashstudy.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// SharedPreferences + Gson ashiglaj deck-uudiig local-d hadgalna
class DeckRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "flashstudy_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    // SharedPreferences-d hadgalah key
    private val DECKS_KEY = "decks_data"

    // deck-uudiig json-aas unshij list burunee butsaana
    fun getDecks(): List<Deck> {
        val json = prefs.getString(DECKS_KEY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Deck>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            // parse aldaa garsun bol khooson list butsaana
            emptyList()
        }
    }

    // buh deck-uudiig json bolgoj SharedPreferences-d bichdeg private function
    private fun saveAllDecks(decks: List<Deck>) {
        val json = gson.toJson(decks)
        prefs.edit().putString(DECKS_KEY, json).apply()
    }

    // shine deck nemeh, odriin deck id-tai tailgaar shinechleh
    fun saveDeck(deck: Deck) {
        val current = getDecks().toMutableList()
        val existingIndex = current.indexOfFirst { it.id == deck.id }
        if (existingIndex >= 0) {
            // bairshin bui deck-iig shinechlene
            current[existingIndex] = deck
        } else {
            // shine deck nemne
            current.add(deck)
        }
        saveAllDecks(current)
    }

    // id-aar deck-iig ustgana
    fun deleteDeck(deckId: String) {
        val current = getDecks().toMutableList()
        current.removeAll { it.id == deckId }
        saveAllDecks(current)
    }

    // id-aar neg deck-iig oll, oldohgui bol null butsaana
    fun getDeckById(deckId: String): Deck? {
        return getDecks().firstOrNull { it.id == deckId }
    }

    // deck dotor card-iig id-aar oll, shine card-aar soliod deck-iig hadgalna
    fun updateCard(deckId: String, updatedCard: FlashCard) {
        val deck = getDeckById(deckId) ?: return
        val updatedCards = deck.cards.map { card ->
            if (card.id == updatedCard.id) updatedCard else card
        }
        val updatedDeck = deck.copy(cards = updatedCards)
        saveDeck(updatedDeck)
    }
}
