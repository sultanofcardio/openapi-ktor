package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

// TODO: Add jwt auth
open class SecurityScheme(
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

