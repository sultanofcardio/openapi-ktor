package com.sultanofcardio.openapi.components

import com.sultanofcardio.Model
import com.sultanofcardio.json
import com.sultanofcardio.openapi.components.securityscheme.SecurityScheme
import org.json.JSONObject

class Components : Model {
    val securitySchemes: MutableMap<String, SecurityScheme> = mutableMapOf()

    override fun json(): JSONObject = json {
        if (securitySchemes.isNotEmpty()) {
            "securitySchemes" to json {
                securitySchemes.forEach { (name, scheme) ->
                    name to scheme.json()
                }
            }
        }
    }
}