package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

class Parameter(
    val `in`: String,
    val name: String,
    val type: String,
    val required: Boolean = true,
    val description: String? = null,
    val example: String? = null,
) : Model {
    override fun json(): JSONObject = json {
        "schema" to json {
            example?.let {
                "example" to it
            }
            "type" to type
        }
        "in" to `in`
        "name" to name
        "required" to required
        description?.let {
            "description" to it
        }
    }
}