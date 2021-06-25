package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.invoke
import com.sultanofcardio.openapi.OpenAPIDoc
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.auth.*
import io.ktor.request.*
import io.ktor.response.*
import org.json.JSONObject

@Suppress("EnumEntryName")
enum class ApiKeyLocation {
    query,
    header,
    cookie
}

class ApiKey(
    name: String,
    val `in`: ApiKeyLocation
) : SecurityScheme(name, "apiKey") {
    override fun json(): JSONObject = super.json {
        "name" to name
        "in" to `in`.name
    }
}

/**
 * Create a basic auth security scheme component that can be used as a configuration
 * to [authenticate]
 */
fun OpenAPIDoc.apiKey(
    name: String,
    `in`: ApiKeyLocation,
    handler: ApiKeyAuthenticationProvider.Configuration.() -> Unit = {}
): ApiKey {
    val apiKey = ApiKey(name, `in`)
    auth.configure {
        apiKeyAuth(name, `in`, handler)
    }
    components.securitySchemes[name] = apiKey
    return apiKey
}

// The following was taken from
// https://github.com/OpenAPITools/openapi-generator/blob/ef26ce68d41ab7da3ce70dfa290022d7ba2112b4/modules/openapi-generator/src/main/resources/kotlin-server/libraries/ktor/ApiKeyAuth.kt.mustache
// and modified to match the built-in authentication providers. Since api key authentication is not supported out of
// the box in ktor, we have to implement it

data class ApiKeyCredential(
    /**
     * The string value of the api key
     */
    val value: String
) : Credential

/**
 * Represents a Api Key authentication provider
 * @param name is the name of the provider, or `null` for a default provider
 */
class ApiKeyAuthenticationProvider internal constructor(config: Configuration) : AuthenticationProvider(config) {

    internal var authenticationFunction: suspend ApplicationCall.(ApiKeyCredential) -> Principal? =
        config.authenticationFunction
    internal var apiKeyName: String = config.apiKeyName
    internal var apiKeyLocation: ApiKeyLocation = config.apiKeyLocation

    class Configuration internal constructor(
        name: String,
        var apiKeyLocation: ApiKeyLocation
    ) : AuthenticationProvider.Configuration(name) {
        internal var authenticationFunction: suspend ApplicationCall.(ApiKeyCredential) -> Principal? = { null }
        var apiKeyName: String = name
        internal val provider: ApiKeyAuthenticationProvider
            get() = ApiKeyAuthenticationProvider(this)

        /**
         * Sets a validation function that will check a given [ApiKeyCredential] instance and return [Principal],
         * or null if credential does not correspond to an authenticated principal
         */
        fun validate(body: suspend ApplicationCall.(ApiKeyCredential) -> Principal?) {
            authenticationFunction = body
        }
    }
}

fun Authentication.Configuration.apiKeyAuth(
    name: String,
    location: ApiKeyLocation,
    configure: ApiKeyAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = ApiKeyAuthenticationProvider.Configuration(name, location).apply(configure).provider
    provider.apiKey()
    register(provider)
}

internal fun ApiKeyAuthenticationProvider.apiKey() {
    pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val credentials = call.request.apiKeyAuthenticationCredentials(apiKeyName, apiKeyLocation)
        val principal = credentials?.let { authenticationFunction(call, it) }

        val cause = when {
            credentials == null -> AuthenticationFailedCause.NoCredentials
            principal == null -> AuthenticationFailedCause.InvalidCredentials
            else -> null
        }

        if (cause != null) {
            context.challenge(apiKeyName, cause) {
                // TODO: Verify correct response structure here.
                call.respond(
                    UnauthorizedResponse(
                        HttpAuthHeader.Parameterized(
                            "API_KEY",
                            mapOf("key" to apiKeyName),
                            HeaderValueEncoding.QUOTED_ALWAYS
                        )
                    )
                )
                it.complete()
            }
        }

        if (principal != null) {
            context.principal(principal)
        }
    }
}

internal fun ApplicationRequest.apiKeyAuthenticationCredentials(
    apiKeyName: String,
    apiKeyLocation: ApiKeyLocation
): ApiKeyCredential? {
    val value: String? = when (apiKeyLocation) {
        ApiKeyLocation.query -> this.queryParameters[apiKeyName]
        ApiKeyLocation.header -> this.headers[apiKeyName]
        ApiKeyLocation.cookie -> this.cookies[apiKeyName]
    }
    return when (value) {
        null -> null
        else -> ApiKeyCredential(value)
    }
}
