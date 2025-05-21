package com.ugurtansal.yemek_tarifleri.roomDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ugurtansal.yemek_tarifleri.model.Tarif
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface TarifDAO {
    //RxJava ile veri akışı sağlamak için Flowable kullanıyoruz.
    //Flowable, veri akışını gözlemlemek için kullanılır.
    //Completable, bir işlemin tamamlandığını bildirmek için kullanılır.
    //Bu durumda, veri ekleme ve silme işlemleri için kullanılır.Geriye değer döndürmez.

    @Query("SELECT * FROM Tarif")
     fun getAll(): Flowable<List<Tarif>>

    @Query("SELECT * FROM Tarif WHERE id = :id")
     fun getById(id: Int): Flowable<Tarif>


    @Insert
    fun insert(tarif: Tarif): Completable


    @Delete
    fun delete(tarif: Tarif): Completable

}