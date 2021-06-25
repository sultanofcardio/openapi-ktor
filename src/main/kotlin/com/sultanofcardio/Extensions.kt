package com.sultanofcardio

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import org.json.JSONObject

/**
 * Syntax sugar for org.json.JSONObject so we can use the setter syntax
 */
operator fun JSONObject.set(key: String, value: Any): JSONObject {
    put(key, value)
    return this
}

/**
 * Convenience function using JSONFactory as a DSL to add properties to an
 * existing [JSONObject]
 */
operator fun JSONObject.invoke(lambda: JSONFactory.() -> Unit): JSONObject {
    val factory = JSONFactory(this)
    lambda(factory)
    return this
}

suspend fun ApplicationCall.respondJSON(
    statusCode: HttpStatusCode = HttpStatusCode.OK,
    handler: JSONFactory.() -> Unit
) {
    val json = json { handler(this) }
    respondText(
        status = statusCode,
        text = json.toString(),
        contentType = ContentType.Application.Json
    )
}