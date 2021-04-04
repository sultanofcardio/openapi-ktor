package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import com.sultanofcardio.openapi.KtorLambda
import org.json.JSONArray
import org.json.JSONObject

open class PathOp(
    var name: String,
    var summary: String = "",
    var description: String = "",
    var security: List<String>? = null,
    var parameters: List<Parameter> = listOf(),
    var requestBody: RequestBody? = null,
    var responses: Responses.() -> Unit = {}
) : Model {

    var handler: KtorLambda = {}
    private val tags: MutableSet<Tag> = mutableSetOf()

    fun tags(vararg tags: Tag) {
        this.tags.addAll(tags)
    }

    fun handle(handler: KtorLambda) {
        this.handler = handler
    }

    override fun json(): JSONObject = json {
        "summary" to summary
        security?.let {
            "security" to it
        }
        "description" to description
        "tags" to JSONArray(tags.map { it.name })
        "parameters" to JSONArray(
            parameters.map { it.json() }
        )
        requestBody?.let {
            "requestBody" to it.json()
        }
        "responses" to json {
            Responses().let {
                responses(it)
                it.responses.forEach { (name, response) ->
                    name.value.toString() to response.json()
                }
            }
        }
    }
}