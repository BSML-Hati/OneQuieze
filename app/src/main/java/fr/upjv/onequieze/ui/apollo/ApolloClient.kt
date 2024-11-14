package fr.upjv.onequieze.ui.apollo

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.Builder().serverUrl("https://graphql.anilist.co").build()