@file:Suppress("FunctionName")

package com.sultanofcardio.openapi.models

import com.sultanofcardio.JSONFactory
import com.sultanofcardio.Model
import com.sultanofcardio.json
import com.sultanofcardio.openapi.openApiSchema
import io.ktor.http.*
import org.json.JSONObject

open class Content(
    val type: ContentType,
    val value: Any
) : Model {
    override fun json(): JSONObject = json {
        type.toString() to json {
            "schema" to value.openApiSchema
        }
    }
}

class PlainTextContent(value: String) : Content(ContentType.Text.Plain, value)

class JsonContent(value: JSONObject) : Content(ContentType.Application.Json, value) {
    constructor(value: Model): this(value.json())
}

fun JsonContent(handler: JSONFactory.() -> Unit): Content = JSONFactory().let {
    handler(it)
    Content(ContentType.Application.Json, it.json)
}