package org.example

import kotlinx.coroutines.withContext
import org.example.data.network.SupabaseClient
import java.io.File
import java.io.FileWriter



suspend fun main() {
    println("Choose the action to perform:\n1 - register a new user\n2 - log in")
    val action = readLine()
    if (action == "1") {
        println(register())
    } else {
        println(logIn())
    }

}

suspend fun register(): String? {
    val email = readLine()
    val password = readLine()
    if (email != null && password != null) {
        try {
            SupabaseClient.createNewUser(email, password)
            val token = SupabaseClient.getCurrentToken()
            if (token != null) {
                saveAccessToken(token)
                try {
                    SupabaseClient.logInViaEmail(email, password)
                    val userId = SupabaseClient.getUserId()
                    return userId
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

suspend fun logIn(): String? {
    val email = readLine()
    val password = readLine()
    if (email != null && password != null) {
        try {
            SupabaseClient.logInViaEmail(email, password)
            val token = SupabaseClient.getCurrentToken()
            if (token != null) {
                saveAccessToken(token)
                val userId = SupabaseClient.getUserId()
                return userId
            } else {
                println("No token received")
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
    return null
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


