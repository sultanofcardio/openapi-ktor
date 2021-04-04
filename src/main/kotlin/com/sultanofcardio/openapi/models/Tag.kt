package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

data class Tag(
    val name: String,
    var description: String? = null
): Model {
    var externalDocs: ExternalDocs? = null

    fun externalDocs(url: String, description: String? = null) {
        externalDocs = ExternalDocs(url).apply {
            this.description = description
        }
    }

    override fun json(): JSONObject = json {
        "name" to name
        description?.let { "description" to it }
        externalDocs?.let { "externalDocs" to it.json() }
    }
}