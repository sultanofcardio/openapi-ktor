package com.sultanofcardio

import org.json.JSONObject

/**
 * DSL class to make creating JSON as easy as creating a map
 */
class JSONFactory(val json: JSONObject = JSONObject()) {
    private val reference = this

    infix fun String.to(value: Any?): JSONFactory {
        json.put(this, value)
        return reference
    }

    operator fun set(key: String, value: Any) {
        json.put(key, value)
    }
}

/**
 * Convenience function using JSONFactory as a DSL to construct a new
 * [JSONObject]
 */
fun json(lambda: JSONFactory.() -> Unit): JSONObject = JSONFactory().let {
    lambda(it)
    it.json
}