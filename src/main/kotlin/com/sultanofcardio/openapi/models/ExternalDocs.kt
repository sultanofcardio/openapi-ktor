package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

class ExternalDocs internal constructor(var url: String): Model {
    var description: String? = null

    override fun json(): JSONObject = json {
        description?.let {
            "description" to it
        }

        "url" to url
    }
}