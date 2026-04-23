package com.example.flashstudy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    DeckEntity::class, 
    FlashCardEntity::class,
    FolderEntity::class,
    FolderDeckCrossRef::class
], version = 2, exportSchema = false)
abstract class FlashStudyDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
}
