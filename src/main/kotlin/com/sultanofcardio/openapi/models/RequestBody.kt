package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

class RequestBody(
    val required: Boolean,
    val content: Content,
    val description: String? = null
) : Model {
    override fun json(): JSONObject = json {
        "required" to required
        "content" to content.json()
        description?.let { "description" to it }
    }
}