package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.Model
import com.sultanofcardio.json
import com.sultanofcardio.openapi.OpenAPIDoc
import org.json.JSONObject

open class SecurityScheme(
    /**
     * The name of the header, query or cookie parameter to be used.
     */
    var name: String,
    var type: String,
    var description: String? = null
) : Model {
    var scheme: String? = null
    var bearerFormat: String? = null
    var openIdConnectUrl: String? = null

    override fun json(): JSONObject = json {
        "type" to type
        description?.let {
            "description" to it
        }
        scheme?.let {
            "scheme" to it
        }
        bearerFormat?.let {
            "bearerFormat" to it
        }
        openIdConnectUrl?.let {
            "openIdConnectUrl" to it
        }
    }
}

fun <T> OpenAPIDoc.getSecurityScheme(name: String): T? {
    val scheme = components.securitySchemes[name]
    return if (scheme != null) {
        try {
            @Suppress("UNCHECKED_CAST")
            scheme as T
        } catch (e: ClassCastException) {
            throw IllegalStateException("Auth configuration $name is already defined as ${scheme::class.simpleName}")
        }
    } else null
}

