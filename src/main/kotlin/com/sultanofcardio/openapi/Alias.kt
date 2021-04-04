package com.sultanofcardio.openapi

import com.sultanofcardio.openapi.models.PathOp
import com.sultanofcardio.openapi.models.RouteHandler
import io.ktor.application.*
import io.ktor.util.pipeline.*

typealias KtorLambda = suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit

typealias SwaggerLambda = PathOp.() -> Unit

typealias RoutingLambda = RouteHandler.() -> Unit