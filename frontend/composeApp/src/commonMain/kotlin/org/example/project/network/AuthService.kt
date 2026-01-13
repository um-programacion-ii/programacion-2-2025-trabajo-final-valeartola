package org.example.project.network

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.example.project.model.LoginResponse

class AuthService(private val httpClient: HttpClient) {
    private val baseUrl = "http://10.186.197.23:8080/api"

    suspend fun loginAsGuest(): LoginResponse? {
        return try {
            val response = httpClient.post("$baseUrl/invitado")

            // AGREGÁ ESTA LÍNEA PARA VER EL ERROR REAL EN EL LOGCAT
            val bodyText = response.bodyAsText()
            println("Cuerpo recibido: $bodyText")

            if (response.status == HttpStatusCode.OK) {
                response.body<LoginResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error de red: ${e.message}")
            null
        }
    }
}