package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.invoke
import com.sultanofcardio.openapi.OpenAPIDoc
import io.ktor.auth.*
import org.json.JSONObject

class BasicAuth(
    name: String
) : SecurityScheme(name, "http") {

    override fun json(): JSONObject = super.json {
        "scheme" to "basic"
    }
}

/**
 * Create a basic auth security scheme component that can be used as a configuration
 * to [authenticate]
 */
fun OpenAPIDoc.basicAuth(name: String, handler: BasicAuthenticationProvider.Configuration.() -> Unit): BasicAuth {
    val basicAuth = BasicAuth(name)
    auth.configure {
        basic(name, handler)
    }
    components.securitySchemes[name] = basicAuth
    return basicAuth
}
