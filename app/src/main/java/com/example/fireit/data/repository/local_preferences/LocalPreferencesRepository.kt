package com.example.fireit.data.repository.local_preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject




class LocalPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    private object PreferencesKeys {
        var HAS_FILES_PERMISSION = booleanPreferencesKey("has_files_permission")

    }

    val hasFilePermission : Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.HAS_FILES_PERMISSION] ?: true }

    suspend fun setFilePermissionSt(st : Boolean){
        dataStore.edit { pref ->
            pref[PreferencesKeys.HAS_FILES_PERMISSION] = st
        }
    }

}