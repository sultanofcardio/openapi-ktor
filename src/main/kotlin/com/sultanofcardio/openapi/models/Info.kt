package com.sultanofcardio.openapi.models

import com.sultanofcardio.Model
import com.sultanofcardio.json
import org.json.JSONObject

class Info : Model {
    var title: String? = null
    var description: String? = null
    var termsOfService: String? = null
    var version: String? = null
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
        version?.let {
            "version" to it
        }
        title?.let {
            "title" to it
        }
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

class Contact internal constructor(): Model {
    var email: String? = null

    override fun json(): JSONObject = json {
        email?.let {
            "email" to it
        }
    }
}

class License internal constructor(): Model {
    var name: String? = null
    var url: String? = null

    override fun json(): JSONObject = json {
        name?.let {
            "name" to it
        }

        url?.let {
            "url" to it
        }
    }
}
