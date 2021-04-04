package com.sultanofcardio.openapi

import com.sultanofcardio.Model
import com.sultanofcardio.json
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.routing.*
import org.json.JSONObject
import java.math.BigDecimal

/**
 * Get the OpenAPI data type of a Kotlin object.
 *
 * @return One of number, string, boolean, object, array
 */
val Any.openApiType: String
    get() {
        return when (this) {
            is Number -> "number"
            is Boolean -> "boolean"
            is Iterable<*> -> "array"
            is String -> "string"
            else -> "object"
        }
    }

val Any.openApiSchema: JSONObject
    get() {
        return when (openApiType) {
            "string", "number", "boolean" -> json {
                "type" to openApiType
                "example" to this@openApiSchema
            }
            "object" -> {
                when(this) {
                    is JSONObject -> this.openApiSchema
                    is Model -> this.json().openApiSchema
                    else -> json {} // TODO: Come up with a better default
                }
            }
            "array" -> (this@openApiSchema as Iterable<*>).openApiSchema
            else -> json {} // TODO: Come up with a better default
        }
    }

val Iterable<*>.openApiSchema: JSONObject
    get() {
        return json {
            "type" to "array"
            if (toList().isNotEmpty()) {
                firstOrNull()?.let { item ->
                    when (item.openApiType) {
                        "string", "number", "boolean" -> {
                            "items" to json {
                                "type" to item.openApiType
                                "example" to item
                            }
                        }
                        "object" -> {
                            "items" to (item as JSONObject).openApiSchema
                        }
                        "array" -> {
                            "items" to (item as Iterable<*>).openApiSchema
                        }
                        else -> null // not used
                    }
                }
            }
        }
    }

val JSONObject.openApiSchema: JSONObject
    get() {
        val json = this
        return json {
            "type" to "object"
            "properties" to json {
                for (key in json.keys()) {
                    when (val value = json[key]) {
                        is String -> {
                            key to json {
                                "type" to "string"
                                "example" to value
                            }
                        }
                        is Float -> {
                            key to json {
                                "type" to "number"
                                "format" to "float"
                                "example" to value
                            }
                        }
                        is Double -> {
                            key to json {
                                "type" to "number"
                                "format" to "double"
                                "example" to value
                            }
                        }
                        is BigDecimal -> {
                            key to json {
                                "type" to "number"
                                "format" to "double"
                                "example" to value.toDouble()
                            }
                        }
                        is Byte, is Short, is Int, is Long -> {
                            key to json {
                                "type" to "integer"
                                "format" to "int64"
                                "example" to value
                            }
                        }
                        is JSONObject -> {
                            key to value.openApiSchema
                        }
                        is Iterable<*> -> {
                            key to value.openApiSchema
                        }
                        is Boolean -> {
                            key to json {
                                "type" to "boolean"
                                "example" to value
                            }
                        }
                    }
                }
            }
        }
    }

internal lateinit var openApiDoc: OpenAPIDoc

@SwaggerDSL
fun Route.openapi(handler: OpenAPIDoc.() -> Unit) {

    if(!::openApiDoc.isInitialized) {
        openApiDoc = OpenAPIDoc(application)
    }

    handler(openApiDoc)
    openApiDoc.swaggerUI()

    // TODO: Find a way to log the connector, host, and port (e.g. http://0.0.0.0:8080/docs/)
    application.log.info("Swagger UI served at /docs/")
}
