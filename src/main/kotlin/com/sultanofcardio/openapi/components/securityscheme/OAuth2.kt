package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.Model
import com.sultanofcardio.invoke
import com.sultanofcardio.openapi.OpenAPIDoc
import com.sultanofcardio.openapi.models.Scopes
import io.ktor.auth.*
import org.json.JSONObject

class OAuth2(
    name: String
): SecurityScheme(name, "oauth2") {

    val flows: OAuthFlows = OAuthFlows()

    override fun json(): JSONObject = super.json {
        "flows" to flows.json()
    }
}

class OAuthFlow : Model {
    var authorizationUrl: String? = null
    var tokenUrl: String? = null
    var refreshUrl: String? = null
    var scopes: Scopes? = null

    fun scopes(map: Scopes.() -> Unit) {
        Scopes().let {
            map(it)
            scopes = it
        }
    }

    override fun json(): JSONObject = com.sultanofcardio.json {
        authorizationUrl?.let {
            "authorizationUrl" to it
        }
        tokenUrl?.let {
            "tokenUrl" to it
        }

        refreshUrl?.let {
            "refreshUrl" to it
        }

        scopes?.let {
            "scopes" to it.json()
        }
    }
}

class OAuthFlows : Model {
    var implicit: OAuthFlow? = null
    var password: OAuthFlow? = null
    var clientCredentials: OAuthFlow? = null
    var authorizationCode: OAuthFlow? = null

    fun implicit(handler: OAuthFlow.() -> Unit) {
        implicit = OAuthFlow().apply(handler)
    }

    fun password(handler: OAuthFlow.() -> Unit) {
        OAuthFlow().let {
            handler(it)
            password = it
        }
    }

    fun clientCredentials(handler: OAuthFlow.() -> Unit) {
        OAuthFlow().let {
            handler(it)
            clientCredentials = it
        }
    }

    fun authorizationCode(handler: OAuthFlow.() -> Unit) {
        OAuthFlow().let {
            handler(it)
            authorizationCode = it
        }
    }

    override fun json(): JSONObject = com.sultanofcardio.json {
        implicit?.let {
            "implicit" to it.json()
        }
        password?.let {
            "password" to it.json()
        }
        clientCredentials?.let {
            "clientCredentials" to it.json()
        }
        authorizationCode?.let {
            "authorizationCode" to it.json()
        }
    }
}

/**
 * Create an OAuth security scheme component that can be used as a configuration
 * to [authenticate]
 */
fun OpenAPIDoc.oauth2(name: String, handler: OAuthAuthenticationProvider.Configuration.(OAuthFlows) -> Unit): OAuth2 {
    val oauth2 = OAuth2(name)
    auth.configure {
        oauth(name) {
            handler(this, oauth2.flows)
        }
    }
    components.securitySchemes[name] = oauth2
    return oauth2
}