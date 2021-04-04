package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

class Response(
    var description: String? = "null",
    var content: Content? = null
) : Model {

    override fun json(): JSONObject = json {
        "description" to description
        content?.let {
            "content" to it.json()
        }
    }
}