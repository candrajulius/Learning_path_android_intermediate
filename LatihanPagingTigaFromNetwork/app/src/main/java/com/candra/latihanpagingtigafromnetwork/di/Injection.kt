package com.candra.latihanpagingtigafromnetwork.di

import android.content.Context
import com.candra.latihanpagingtigafromnetwork.data.QuoteRepository
import com.candra.latihanpagingtigafromnetwork.database.QuoteDatabase
import com.candra.latihanpagingtigafromnetwork.network.ApiConfig

object Injection {

    fun provideRepository(context: Context): QuoteRepository {
        val database = QuoteDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return QuoteRepository(database, apiService)
    }

}