package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.invoke
import com.sultanofcardio.openapi.OpenAPIDoc
import com.sultanofcardio.openapi.models.RouteHandler
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import org.json.JSONObject

/**
 * The OpenAPI security scheme of type 'http' and scheme 'bearer', which accepts a JSON Web Token
 *
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#security-scheme-object">Security Scheme Object</a>
 * @see <a href="https://jwt.io/">JSON Web Token</a>
 */
class JWTAuth(
    name: String
) : SecurityScheme(name, "http") {
    override fun json(): JSONObject = super.json().invoke {
        "scheme" to "bearer"
    }
}

/**
 * Create a JWT security scheme component that can be used as a configuration
 * to [RouteHandler.authenticate]
 */
fun OpenAPIDoc.jwtAuth(name: String, handler: JWTAuthenticationProvider.Configuration.() -> Unit): JWTAuth {
    var jwtAuth = getSecurityScheme<JWTAuth>(name)
    if(jwtAuth != null) return jwtAuth
    jwtAuth = JWTAuth(name)
    auth.configure {
        jwt(name, handler)
    }
    components.securitySchemes[name] = jwtAuth
    return jwtAuth
}