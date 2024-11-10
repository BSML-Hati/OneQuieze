package fr.upjv.onequieze.ui.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloRequest
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.interceptor.ApolloInterceptor
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class LoggingApolloInterceptor: ApolloInterceptor {
    override fun <D : Operation.Data> intercept(request: ApolloRequest<D>, chain: ApolloInterceptorChain): Flow<ApolloResponse<D>> {
        return chain.proceed(request).onEach { response ->
            println("Received response for ${request.operation.name()}: ${response.data}")

            // Vérification des erreurs
            if (response.errors?.any { it.message.contains("Too Many Requests") } == true) {
                println("Erreur 429: Trop de requêtes envoyées")
                // Tu pourrais ajouter ici un mécanisme de gestion de l'erreur (par exemple, un délai avant de réessayer)
            }
        }
    }
}


val apolloClient = ApolloClient.Builder()
    .serverUrl("https://graphql.anilist.co")
    .addInterceptor(LoggingApolloInterceptor())
    .build()