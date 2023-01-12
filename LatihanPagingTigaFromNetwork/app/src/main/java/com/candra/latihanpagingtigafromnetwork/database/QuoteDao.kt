package com.candra.latihanpagingtigafromnetwork.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.candra.latihanpagingtigafromnetwork.network.QuoteResponseItem

@Dao
interface QuoteDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserQuote(quote: List<QuoteResponseItem>)

    @Query("SELECT * FROM quote")
    fun getAllQuote(): PagingSource<Int,QuoteResponseItem>

    @Query("DELETE FROM quote")
    suspend fun deleteAll()
}