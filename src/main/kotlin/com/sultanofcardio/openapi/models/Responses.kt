package com.sultanofcardio.openapi.models

import io.ktor.http.*

class Responses {
    val responses: MutableList<Pair<HttpStatusCode, Response>> = mutableListOf()

    infix fun HttpStatusCode.to(response: Response) {
        responses.add(Pair(this, response))
    }

    fun response(
        status: HttpStatusCode = HttpStatusCode.OK,
        handler: Response.() -> Unit
    ) {
        Response().let {
            handler(it)
            status to it
        }
    }
}