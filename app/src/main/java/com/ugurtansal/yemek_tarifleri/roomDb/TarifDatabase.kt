package com.ugurtansal.yemek_tarifleri.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ugurtansal.yemek_tarifleri.model.Tarif

@Database(entities = [Tarif::class], version =1)
abstract class TarifDatabase : RoomDatabase() {
    abstract fun tarifDao(): TarifDAO
}