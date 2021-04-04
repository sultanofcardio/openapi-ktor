@file:Suppress("NAME_SHADOWING")

package com.sultanofcardio.openapi.models

import com.sultanofcardio.openapi.*
import com.sultanofcardio.openapi.components.securityscheme.SecurityScheme
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

class AuthConfig(
    vararg val configurations: SecurityScheme = arrayOf(),
    val optional: Boolean = false,
)

open class RouteHandler internal constructor(
    val application: Application,
    val authConfig: AuthConfig? = null,
    internal val tags: MutableList<Tag> = mutableListOf(),
    val documentedBasePath: String = "",
    val realBasePath: String = ""
) {
    val paths: MutableMap<String, MutableSet<PathOp>> = mutableMapOf()
    val authenticatedRouters = mutableSetOf<AuthenticatedRouteHandler>()
    var nestedRouters = mutableSetOf<RouteHandler>()
    open val consolidatedPaths: MutableMap<String, MutableSet<PathOp>>
        get() = paths.apply {
            nestedRouters.forEach { putAll(it.consolidatedPaths) }
            authenticatedRouters.forEach { putAll(it.consolidatedPaths) }
        }

    /**
     * Serve up a Swagger UI page with these docs
     */
    open fun swaggerUI() {
        val pathWithTrailingSlash = "/docs/"
        val pathWithoutTrailingSlash = pathWithTrailingSlash.dropLastWhile { it == '/' }
        application.routing {
            fun Route.docsEndpoint() {
                get("$pathWithoutTrailingSlash/openapi.json") {
                    call.respond(openApiDoc.toString())
                }

                // TODO: Add a YAML endpoint

                static(pathWithTrailingSlash) {
                    resources("docs")
                    defaultResource("docs/index.html")
                }
                get(pathWithoutTrailingSlash) {
                    call.respondRedirect(pathWithTrailingSlash)
                }
            }

            if (authConfig != null) {
                authenticate(
                    *authConfig.configurations.map { it.name }.toTypedArray(),
                    optional = authConfig.optional
                ) {
                    docsEndpoint()
                }
            } else {
                docsEndpoint()
            }
        }
    }

    fun tag(name: String, description: String? = null, handler: Tag.() -> Unit = {}): Tag {
        return Tag(name, description).apply {
            handler()
            tags.add(this)
        }
    }

    /**
     * Add authenticated routes
     */
    open fun authenticate(
        vararg configurations: SecurityScheme = arrayOf(),
        optional: Boolean = false,
        build: AuthenticatedRouteHandler.() -> Unit
    ) {
        val authenticated = AuthenticatedRouteHandler(
            application, AuthConfig(*configurations, optional = optional), tags, documentedBasePath, realBasePath
        )
        authenticated.build()
        authenticatedRouters.add(authenticated)
    }

    /**
     * Create a nested route handler
     */
    @SwaggerDSL
    open fun route(path: String, handler: RoutingLambda) {
        val router = RouteHandler(
            application, authConfig, tags, documentedBasePath + path, realBasePath + path
        )
        router.handler()
        nestedRouters.add(router)
    }

    @SwaggerDSL
    open fun undocumentedRoute(path: String, handler: RoutingLambda) {
        val router = RouteHandler(
            application, authConfig, tags, documentedBasePath, realBasePath + path
        )
        router.handler()
        nestedRouters.add(router)
    }

    @SwaggerDSL
    open fun put(path: String = "", handler: SwaggerLambda) {
        PathOp("put").apply {
            val realPath = realBasePath + path
            val path = documentedBasePath + path

            handler(this)
            if (authConfig != null) {
                security = authConfig.configurations.map { it.name }
            }
            if (paths[path] != null) {
                paths[path]!!.add(this)
            } else {
                paths[path] = mutableSetOf(this)
            }
            application.routing {
                if (authConfig != null) {
                    authenticate(
                        *authConfig.configurations.map { it.name }.toTypedArray(),
                        optional = authConfig.optional
                    ) {
                        put(realPath) {
                            this.handler(it)
                        }
                    }
                } else {
                    put(realPath) {
                        this.handler(it)
                    }
                }
            }
        }
    }

    @SwaggerDSL
    open fun post(path: String = "", handler: SwaggerLambda) {
        PathOp("post").apply {
            val realPath = realBasePath + path
            val path = documentedBasePath + path

            handler(this)
            if (authConfig != null) {
                security = authConfig.configurations.map { it.name }
            }
            if (paths[path] != null) {
                paths[path]!!.add(this)
            } else {
                paths[path] = mutableSetOf(this)
            }
            application.routing {
                if (authConfig != null) {
                    authenticate(
                        *authConfig.configurations.map { it.name }.toTypedArray(),
                        optional = authConfig.optional
                    ) {
                        post(realPath) {
                            this.handler(it)
                        }
                    }
                } else {
                    post(realPath) {
                        this.handler(it)
                    }
                }
            }
        }
    }

    @SwaggerDSL
    open fun get(path: String = "", handler: SwaggerLambda) {
        PathOp("get").apply {
            val realPath = realBasePath + path
            val path = documentedBasePath + path

            handler(this)
            if (authConfig != null) {
                security = authConfig.configurations.map { it.name }
            }
            if (paths[path] != null) {
                paths[path]!!.add(this)
            } else {
                paths[path] = mutableSetOf(this)
            }
            application.routing {
                if (authConfig != null) {
                    authenticate(
                        *authConfig.configurations.map { it.name }.toTypedArray(),
                        optional = authConfig.optional
                    ) {
                        get(realPath) {
                            this.handler(it)
                        }
                    }
                } else {
                    get(realPath) {
                        this.handler(it)
                    }
                }
            }
        }
    }

    @SwaggerDSL
    open fun delete(path: String = "", handler: SwaggerLambda) {
        PathOp("delete").apply {
            val realPath = realBasePath + path
            val path = documentedBasePath + path

            handler(this)
            if (authConfig != null) {
                security = authConfig.configurations.map { it.name }
            }
            if (paths[path] != null) {
                paths[path]!!.add(this)
            } else {
                paths[path] = mutableSetOf(this)
            }
            application.routing {
                if (authConfig != null) {
                    authenticate(
                        *authConfig.configurations.map { it.name }.toTypedArray(),
                        optional = authConfig.optional
                    ) {
                        delete(realPath) {
                            this.handler(it)
                        }
                    }
                } else {
                    delete(realPath) {
                        this.handler(it)
                    }
                }
            }
        }
    }
}

class AuthenticatedRouteHandler(
    application: Application,
    authConfig: AuthConfig,
    tags: MutableList<Tag>,
    documentedBasePath: String = "",
    realBasePath: String = ""
) : RouteHandler(application, authConfig, tags, documentedBasePath, realBasePath)
