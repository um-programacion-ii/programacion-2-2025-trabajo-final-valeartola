package org.example.project.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.model.LoginResponse

class AuthRepository {
    // Configuramos el cliente Ktor con JSON
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Evita errores si el backend manda campos extra
            })
        }
    }

    private val baseUrl = "http://10.186.197.23:8080/api"

    suspend fun loginAsGuest(): LoginResponse? {
        return try {
            val response = httpClient.post("$baseUrl/invitado")
            val bodyText = response.bodyAsText() // <--- AGREGÁ ESTO
            println("JSON CRUDO DEL SERVIDOR: $bodyText") // <--- Y ESTO

            if (response.status == HttpStatusCode.OK) {
                response.body<LoginResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }
}