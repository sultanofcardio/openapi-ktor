package com.sultanofcardio

import org.json.JSONObject

interface Model {
    fun json(): JSONObject

    val json: JSONObject get() = json()
}