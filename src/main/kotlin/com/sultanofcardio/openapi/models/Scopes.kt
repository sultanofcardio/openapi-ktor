package com.sultanofcardio.openapi.models

import com.sultanofcardio.JSONFactory
import com.sultanofcardio.Model
import org.json.JSONObject

class Scopes : Model {
    val scopes: JSONFactory = JSONFactory()

    infix fun String.to(other: String) {
        scopes[this] = other
    }

    override fun json(): JSONObject = scopes.json
}