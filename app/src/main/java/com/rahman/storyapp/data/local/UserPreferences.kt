package com.rahman.storyapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    val getUser: Flow<UserModel> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else throw exception
        }.map { pref ->
            UserModel(pref[name] ?: "", pref[uid] ?: "", pref[token] ?: "")
        }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { pref ->
            pref[name] = user.name
            pref[uid] = user.userId
            pref[token] = user.token
        }
    }

    suspend fun logout() {
        dataStore.edit { preference ->
            preference.remove(name)
            preference.remove(uid)
            preference.remove(token)
            preference.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val name = stringPreferencesKey("session_name")
        private val uid = stringPreferencesKey("session_uid")
        private val token = stringPreferencesKey("session_token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}