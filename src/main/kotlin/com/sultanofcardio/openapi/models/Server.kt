package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONArray
import org.json.JSONObject

class Server(
    val url: String,
    var description: String? = null
) : Model {
    private val variables = mutableMapOf<String, ServerVariable>()

    fun variable(name: String, default: String, handler: ServerVariable.() -> Unit = {}) {
        require(url.contains("{$name}")) { "Invalid variable $name is not present in url $url" }
        variables[name] = ServerVariable(default).apply(handler)
    }

    override fun json(): JSONObject = json {
        "url" to url
        description?.let { "description" to it }
        "variables" to json {
            variables.forEach { (name, value) ->
                name to value.json()
            }
        }
    }
}

class ServerVariable(
    val default: String
) : Model {

    var description: String? = null
    private var enum = listOf<String>()

    fun enum(vararg values: String) {
        enum = listOf(*values)
    }

    override fun json(): JSONObject = json {
        "default" to default
        description?.let { "description" to it }
        if(enum.isNotEmpty()) {
            "enum" to JSONArray(enum)
        }
    }
}