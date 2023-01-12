package com.candra.propertyanimation.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPrefrence private constructor(private val dataStore: DataStore<Preferences>)
{
    fun getUser(): Flow<UserModel>{
        return dataStore.data.map {
            pr
        }
    }
}