package com.neon.services;

import com.azure.ai.openai.OpenAIClient
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.core.credential.AzureKeyCredential
import com.azure.cosmos.CosmosClientBuilder
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions
import org.springframework.ai.document.Document
import org.springframework.ai.document.MetadataMode
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.CosmosDBVectorStore
import org.springframework.ai.vectorstore.CosmosDBVectorStoreConfig
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.*
import java.util.Map
import kotlin.collections.List

@Service
class ContractEmbeddingService {
    @Value("\${spring.ai.azure.openai.endpoint}")
    lateinit var openAiEndpoint: String

    @Value("\${spring.ai.azure.openai.api-key}")
    lateinit var openAiKey: String

    @Lazy
    @Autowired
    private val vectorStore: VectorStore? = null


    private val openAiClient by lazy {
        OpenAIClientBuilder().endpoint(openAiEndpoint).credential(AzureKeyCredential(openAiKey)).buildClient()
    }

    @Bean
    fun observationRegistry(): ObservationRegistry {
        return ObservationRegistry.create()
    }

    @Bean
    fun vectorStore(observationRegistry: ObservationRegistry?): VectorStore {
        val config = CosmosDBVectorStoreConfig()
        config.databaseName = "neon-cosmosdb-database"
        config.containerName = "neon-cosmosdb-container"
        config.metadataFields = "language"
        config.vectorStoreThroughput = 400

        val cosmosClient = CosmosClientBuilder()
            .endpoint("https://neon-cosmosdb.documents.azure.com:443/")
            .userAgentSuffix("SpringAI-CDBNoSQL-VectorStore")
            .key("qNR6UUukfcuFYtDYqwrzU0XTxxWYYdesEKofNvZxldB01wjBWeY5PF94iut1g2cjLARVaQVutizcACDbELnU6g")
            .gatewayMode()
            .buildAsyncClient()

        return CosmosDBVectorStore(
            observationRegistry,
            null,
            cosmosClient,
            config,
            embeddingModel(openAiClient)
        )
    }

    fun embeddingModel(openAIClient: OpenAIClient?): EmbeddingModel {
        val options = AzureOpenAiEmbeddingOptions.builder().withDeploymentName("embedding-model").build()
        val embeddingModel = AzureOpenAiEmbeddingModel(openAIClient, MetadataMode.EMBED, options)
        return embeddingModel
    }

    fun store(contractName: String, content: String, heading: String, pageNumber: Number, version: String?) {
        val document =
            Document(UUID.randomUUID().toString(), content, Map.of<String, Any>("contractName",contractName,"heading", heading, "pageNumber", pageNumber, "version", version))
        vectorStore!!.add(listOf(document))
    }

    fun search(query: String?, contractName: String?, version: String?): List<String> {
        val searchRequest = SearchRequest.query(query).withTopK(2)
        searchRequest.withSimilarityThreshold(0.8)
       // searchRequest.withFilterExpression("metadata.contractName='$contractName' AND metadata.version='$version'")
        val results = vectorStore!!.similaritySearch(searchRequest)
        return results.map { it.content }
    }
}
