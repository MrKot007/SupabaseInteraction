package org.example

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConnection {

    val supabase = createSupabaseClient(
        supabaseUrl = "",
        supabaseKey = ""
    ) {
        install(Auth)
        install(Postgrest)
    }


}