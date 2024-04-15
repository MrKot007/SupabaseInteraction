package org.example

import io.github.jan.supabase.gotrue.user.UserInfo
import org.example.data.network.SupabaseClient
import java.io.File
import java.io.FileWriter



suspend fun main() {
    println("Choose the action to perform:\n1 - register a new user\n2 - log in")
    val action = readLine()
    if (action == "1") {
        val userInfo = register()
        if (userInfo != null) {
            val userId = userInfo.id
            val userMetadata = userInfo.userMetadata
            println("User ID: $userId")
            println("User metadata: $userMetadata")
        } else {
            println("No user found")
        }

    } else {
        val userInfo = logIn()
        if (userInfo != null) {
            val userId = userInfo.id
            val userMetadata = userInfo.userMetadata
            println("User ID: $userId")
            println("User metadata: $userMetadata")
        } else {
            println("No user found")
        }
    }
    println("Log out? (Y/N)")
    val logout = readLine()
    if (logout == "Y") {
        signOut()
        try {
            val user = SupabaseClient.getUser()
            if (user != null){
                println(user.id)
            }

        } catch (e: Exception) {
            println(e.message)
        }
    } else {
        println("ну ладно")
    }

}

suspend fun register(): UserInfo? {
    val email = readLine()
    val password = readLine()
    if (email != null && password != null) {
        try {
            SupabaseClient.createNewUser(email, password)
            val token = SupabaseClient.getCurrentToken()
            if (token != null) {
                saveAccessToken(token)
                try {
                    val user = SupabaseClient.getUser()
                    return user
                } catch (e: Exception) {
                    println("Authentication upon registration failed due to: ${e.message}")
                }

            } else {
                println("No token received")
            }
        } catch (e: Exception) {
            println(e.message)
        }

    }
    return null


}

suspend fun logIn(): UserInfo? {
    val email = readLine()
    val password = readLine()
    if (email != null && password != null) {
        try {
            SupabaseClient.logInViaEmail(email, password)
            val token = SupabaseClient.getCurrentToken()
            if (token != null) {
                saveAccessToken(token)
                val user = SupabaseClient.getUser()
                return user
            } else {
                println("No token received")
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
    return null
}

suspend fun signOut() {
    try {
        if (SupabaseClient.getCurrentToken() != null) {
            SupabaseClient.signOut()
            println("Signout performed, session unauthorized")
        }
    } catch (e: Exception) {
        println(e.message)
    }

}

fun saveAccessToken(token: String) {
    val path = "token.txt"
    try {
        val file = File(path)
        val writer = FileWriter(file)
        if (file.exists()) {
            writer.write(token)
            writer.close()
        } else {
            file.createNewFile()
            writer.write(token)
            writer.close()
        }

    } catch (e: Exception) {
        println("Failed to create a new file due to: ${e.message}")
    }

}

fun getAccessToken(): String? {
    val path = "token.txt"
    try {
        val file = File(path)
        val token = file.readText()
        return token
    } catch (e: Exception) {
        println("Failed to fetch the token due to: ${e.message}")
        return null
    }
}


