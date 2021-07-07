package com.sultanofcardio.openapi

import com.sultanofcardio.Model
import com.sultanofcardio.json
import com.sultanofcardio.openapi.components.Components
import com.sultanofcardio.openapi.models.ExternalDocs
import com.sultanofcardio.openapi.models.Info
import com.sultanofcardio.openapi.models.RouteHandler
import com.sultanofcardio.openapi.models.Server
import io.ktor.application.*
import io.ktor.auth.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * An OpenAPI document object.
 *
 * @author sultanofcardio
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oasObject">OpenAPI Object</a>
 */
class OpenAPIDoc(
    application: Application,
    basePath: String = ""
) : Model, RouteHandler(application, documentedBasePath = basePath) {

    val info: Info = Info()
    var externalDocs: ExternalDocs? = null
    val components: Components = Components()
    val servers = mutableListOf<Server>()
    val auth = application.let {
        it.featureOrNull(Authentication) ?: it.install(Authentication) {}
    }

    /**
     * Security schemes that apply to all routes
     */
    var security: List<String> = listOf()
    var openapi: String = "3.0.3"

    fun externalDocs(url: String, description: String? = null) {
        externalDocs = ExternalDocs(url).apply {
            this.description = description
        }
    }

    fun server(url: String, description: String? = null, handler: Server.() -> Unit = {}) {
        servers.add(Server(url, description).apply(handler))
    }

    fun info(handler: Info.() -> Unit) {
        info.handler()
    }

    override fun json(): JSONObject = json {
        "openapi" to openapi
        "info" to info.json()
        "servers" to JSONArray(servers.distinctBy { it.url }.map { it.json() })
        "tags" to JSONArray(tags.distinctBy { it.name }.map { it.json() })
        externalDocs?.let {
            "externalDocs" to it.json()
        }
        "security" to security.map {
            json {
                it to emptyList<Any>()
            }
        }
        "paths" to json {
            val sorted = consolidatedPaths.toSortedMap()
            sorted.forEach { (name, ops) ->
                name.ifBlank { "/" } to json {
                    ops.distinctBy { op -> op.name }.forEach { op ->
                        op.name to op.json()
                    }
                }
            }
        }
        "components" to components.json()
    }

    override fun toString(): String = json().toString()
}