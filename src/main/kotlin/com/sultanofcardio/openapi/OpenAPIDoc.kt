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
import com.sultanofcardio.openapi.components.securityscheme.BasicAuth

/**
 * An OpenAPI document object. This is the root document object of the [OpenAPI document](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oasDocument).
 *
 * @author [sultanofcardio](https://github.com/sultanofcardio)
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oasObject">OpenAPI Object</a>
 */
class OpenAPIDoc(
    application: Application,
    basePath: String = ""
) : Model, RouteHandler(application, documentedBasePath = basePath) {

    /**
     * Provides metadata about the API. The metadata MAY be used by tooling as required.
     *
     * You must configure this property using [info] and set the title and version properties
     */
    val info: Info = Info()

    /**
     * Additional external documentation
     */
    var externalDocs: ExternalDocs? = null

    /**
     * An element to hold various schemas for the specification.
     * Currently only the securitySchemes schema is supported
     */
    val components: Components = Components()

    /**
     * An array of Server Objects, which provide connectivity information to a
     * target server. If the servers property is not provided, or is an empty array,
     * the default value would be a Server Object with a url value of /.
     */
    val servers = mutableListOf<Server>()

    /**
     * A local property reference to the [Authentication] ktor feature. This allows various
     * securityScheme providers to configure authentication. e.g. [BasicAuth]
     */
    val auth = application.let {
        it.featureOrNull(Authentication) ?: it.install(Authentication) {}
    }

    /**
     * Security schemes that apply to all routes
     */
    var security: List<String> = listOf()

    /**
     * The semantic version number of the OpenAPI Specification version that the OpenAPI document uses
     */
    var openapi: String = "3.0.3"

    /**
     * Configure the [externalDocs] property
     */
    fun externalDocs(url: String, description: String? = null) {
        externalDocs = ExternalDocs(url).apply {
            this.description = description
        }
    }

    /**
     * Configure and add a [Server] to the list of [servers]
     */
    fun server(url: String, description: String? = null, handler: Server.() -> Unit = {}) {
        servers.add(Server(url, description).apply(handler))
    }

    /**
     * Configure the info property
     */
    fun info(handler: Info.() -> Unit) {
        info.handler()
    }

    /**
     * Generate a valid OpenAPI json document from the configured object
     */
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

    /**
     * Converts the JSON generated by [json] into a compact string
     */
    override fun toString(): String = json().toString()
}