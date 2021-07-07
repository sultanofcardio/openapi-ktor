package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.invoke
import com.sultanofcardio.openapi.OpenAPIDoc
import io.ktor.auth.*
import org.json.JSONObject
import com.sultanofcardio.openapi.models.RouteHandler

/**
 * The OpenAPI security scheme of type 'http' and scheme 'basic'
 *
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#security-scheme-object">Security Scheme Object</a>
 */
class BasicAuth(
    name: String
) : SecurityScheme(name, "http") {

    override fun json(): JSONObject = super.json().invoke {
        "scheme" to "basic"
    }
}

/**
 * Create a basic auth security scheme component that can be used as a configuration
 * to [RouteHandler.authenticate]
 */
fun OpenAPIDoc.basicAuth(name: String, handler: BasicAuthenticationProvider.Configuration.() -> Unit): BasicAuth {
    var basicAuth = getSecurityScheme<BasicAuth>(name)
    if(basicAuth != null) return basicAuth
    basicAuth = BasicAuth(name)
    auth.configure {
        basic(name, handler)
    }
    components.securitySchemes[name] = basicAuth
    return basicAuth
}
