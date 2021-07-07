package com.sultanofcardio.openapi.components.securityscheme

import com.sultanofcardio.Model
import com.sultanofcardio.json
import com.sultanofcardio.invoke
import com.sultanofcardio.openapi.OpenAPIDoc
import com.sultanofcardio.openapi.models.RouteHandler
import com.sultanofcardio.openapi.models.Scopes
import io.ktor.auth.*
import org.json.JSONObject

/**
 * The OpenAPI security scheme of type 'oauth2'
 *
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#security-scheme-object">Security Scheme Object</a>
 */
class OAuth2(
    name: String
): SecurityScheme(name, "oauth2") {

    val flows: OAuthFlows = OAuthFlows()

    override fun json(): JSONObject = super.json().invoke {
        "flows" to flows.json()
    }
}

/**
 * Configuration details for a supported OAuth Flow
 *
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oauthFlowObject">OAuth Flow Object</a>
 */
class OAuthFlow : Model {
    /**
     * The authorization URL to be used for this flow. This MUST be in the form of a URL.
     */
    lateinit var authorizationUrl: String

    /**
     * The token URL to be used for this flow. This MUST be in the form of a URL.
     */
    lateinit var tokenUrl: String

    /**
     * The available scopes for the OAuth2 security scheme. A map between the scope name and a
     * short description for it. The map MAY be empty.
     */
    lateinit var flowScopes: Scopes

    /**
     * The URL to be used for obtaining refresh tokens. This MUST be in the form of a URL.
     */
    var refreshUrl: String? = null

    fun scopes(map: Scopes.() -> Unit) {
        Scopes().let {
            map(it)
            flowScopes = it
        }
    }

    override fun json(): JSONObject = json {
        check(this@OAuthFlow::authorizationUrl.isInitialized) { "Please set a value for the authorizationUrl property" }
        check(this@OAuthFlow::tokenUrl.isInitialized) { "Please set a value for the tokenUrl property" }
        check(this@OAuthFlow::flowScopes.isInitialized) { "Please configure the scopes property" }

        "authorizationUrl" to authorizationUrl
        "scopes" to flowScopes.json()
        "tokenUrl" to tokenUrl
        refreshUrl?.let {
            "refreshUrl" to it
        }
    }
}

/**
 * Allows configuration of the supported OAuth Flows.
 *
 * @see <a href="https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#oauth-flows-object">OAuth Flows Object</a>
 */
class OAuthFlows : Model {

    /**
     * Configuration for the OAuth Implicit flow
     */
    var implicit: OAuthFlow? = null

    /**
     * Configuration for the OAuth Resource Owner Password flow
     */
    var password: OAuthFlow? = null

    /**
     * Configuration for the OAuth Client Credentials flow. Previously called application in OpenAPI 2.0.
     */
    var clientCredentials: OAuthFlow? = null

    /**
     * Configuration for the OAuth Authorization Code flow. Previously called accessCode in OpenAPI 2.0.
     */
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
 * to [RouteHandler.authenticate]
 */
fun OpenAPIDoc.oauth2(name: String, handler: OAuthAuthenticationProvider.Configuration.(OAuthFlows) -> Unit): OAuth2 {
    var oauth2 = getSecurityScheme<OAuth2>(name)
    if(oauth2 != null) return oauth2
    oauth2 = OAuth2(name)
    auth.configure {
        oauth(name) {
            handler(this, oauth2.flows)
        }
    }
    components.securitySchemes[name] = oauth2
    return oauth2
}