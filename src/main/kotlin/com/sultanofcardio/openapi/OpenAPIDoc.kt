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
 * TODO: Change this to actual description docs
 * This is a hacky attempts to serve swagger docs from Ktor. I threw this together in a few days because
 * there's no official support from Ktor, the team isn't really interested in it and I'm not looking to
 * adopt a third-party unofficial method.
 *
 * This was the fastest and most flexible way IMO to get this done. The goal was to integrate the docs
 * with the routing declaration and share the models being used in the actual code. That way, we don't
 * have to maintain two separate things
 *
 * @author sultanofcardio
 */
class OpenAPIDoc(
    application: Application,
    basePath: String = ""
) : Model, RouteHandler(application, documentedBasePath = basePath) {

    var info: Info? = null
    internal var externalDocs: ExternalDocs? = null
    internal val components: Components = Components()
    internal val servers = mutableListOf<Server>()
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
        Info().let {
            handler(it)
            info = it
        }
    }

    override fun json(): JSONObject = json {
        "openapi" to openapi
        info?.let {
            "info" to it.json()
        }
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