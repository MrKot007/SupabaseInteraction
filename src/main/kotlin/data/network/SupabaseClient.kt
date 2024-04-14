package org.example.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import org.example.getAccessToken
import org.example.saveAccessToken

object SupabaseClient {

    private val supabase = createSupabaseClient(
        supabaseUrl = "https://pxeijhmevncadtcbtptq.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB4ZWlqaG1ldm5jYWR0Y2J0cHRxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTMxMjM4MDIsImV4cCI6MjAyODY5OTgwMn0.dzP0ml9nllTiCADUnZk_6OcT6bHsonhw2tEN_qhfhVM"
    ) {
        install(Auth)
        install(Postgrest)
    }

    private val auth = supabase.auth

    suspend fun createNewUser(mail: String, password: String) {
        auth.signUpWith(Email) {
            this.email = mail
            this.password = password
        }
    }

    suspend fun logInViaEmail(mail: String, password: String) {
        auth.signInWith(Email) {
            this.email = mail
            this.password = password
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun getCurrentToken(): String? {
        return auth.currentAccessTokenOrNull()
    }

    suspend fun getUserId(): String? {
        try {
            val token = getAccessToken()
            if (token.isNullOrEmpty()) {
                println("Unauthorized session")
            } else {
                val user = auth.retrieveUser(token)
                auth.refreshCurrentSession()
                saveAccessToken(token)
                println("The user is authorized")
                return user.id
            }
        } catch (e: Exception) {
            println(e.message)
            return null
        }
        return null
    }


}