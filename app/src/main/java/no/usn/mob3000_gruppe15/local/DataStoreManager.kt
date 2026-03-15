package no.usn.mob3000_gruppe15.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Hjelp av KI for å løse problem ved at bruker må logge inn hver gang,
 * fremfor å forbli innlogget (lagre status på brukers telefon)
 *
 * Kan oppdateres senere, men nåværende utkast brukes først og fremst
 * for å la oss (utviklere) unngå å måtte logge inn hver gang.
 */
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val ID = stringPreferencesKey("id")
    }

    // Lagre innloggingsstatus
    suspend fun saveLoginStatus(isloggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = isloggedIn
        }
    }

    //lagre navn og id
    suspend fun saveLoginData(id: String?) {
        context.dataStore.edit { prefs ->
            id?.let { prefs[ID] = it }
        }
    }

    // Hente innloggingsstatus
    val loginStatus: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    val id: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ID]
    }

    // 'Logg ut' funksjon
    suspend fun clearLoginStatus() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}