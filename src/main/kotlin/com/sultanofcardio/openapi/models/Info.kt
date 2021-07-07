package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

class Info : Model {
    lateinit var title: String
    lateinit var version: String
    var description: String? = null
    var termsOfService: String? = null
    private var contact: Contact? = null
    private var license: License? = null

    fun contact(handler: Contact.() -> Unit) {
        contact = Contact().apply(handler)
    }

    fun license(name: String, url: String) {
        license = License().apply {
            this.name = name
            this.url = url
        }
    }

    override fun json(): JSONObject = json {
        check(this@Info::title.isInitialized) { "Please set a value for the title property" }
        check(this@Info::version.isInitialized) { "Please set a value for the version property" }

        "version" to version
        "title" to title

        description?.let {
            "description" to it
        }
        termsOfService?.let {
            "termsOfService" to it
        }
        contact?.let {
            "contact" to it.json()
        }
        license?.let {
            "license" to it.json()
        }
    }
}

class Contact : Model {
    var email: String? = null

    override fun json(): JSONObject = json {
        email?.let {
            "email" to it
        }
    }
}

class License : Model {
    lateinit var name: String
    var url: String? = null

    override fun json(): JSONObject = json {
        check(this@License::name.isInitialized) { "Please set a value for the name property" }

        "name" to name
        url?.let {
            "url" to it
        }
    }
}
