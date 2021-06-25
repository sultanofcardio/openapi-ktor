package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.invoke
import com.sultanofcardio.openapi.OpenAPIDoc
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.json.JSONObject

class JWTAuth(
    name: String
): SecurityScheme(name, "http") {
    override fun json(): JSONObject = super.json {
        "scheme" to "bearer"
    }
}

/**
 * Create a JWT security scheme component that can be used as a configuration
 * to [authenticate]
 */
fun OpenAPIDoc.jwtAuth(name: String, handler: JWTAuthenticationProvider.Configuration.() -> Unit): JWTAuth {
    val jwtAuth = JWTAuth(name)
    auth.configure {
        jwt(name, handler)
    }
    components.securitySchemes[name] = jwtAuth
    return jwtAuth
}