package com.neon.services;




class ContractEmbeddingService {
//    @Value("\${spring.ai.azure.openai.endpoint}")
//    lateinit var openAiEndpoint: String
//
//    @Value("\${spring.ai.azure.openai.api-key}")
//    lateinit var openAiKey: String
//
//    @Lazy
//    @Autowired
//    private val vectorStore: VectorStore? = null
//
//
//    private val openAiClient by lazy {
//        OpenAIClientBuilder().endpoint(openAiEndpoint).credential(AzureKeyCredential(openAiKey)).buildClient()
//    }
//
//    @Bean
//    fun observationRegistry(): ObservationRegistry {
//        return ObservationRegistry.create()
//    }
//
//    @Bean
//    fun vectorStore(observationRegistry: ObservationRegistry?): VectorStore {
//        val config = CosmosDBVectorStoreConfig()
//        config.databaseName = "neon-cosmosdb-database"
//        config.containerName = "neon-cosmosdb-container"
//        config.metadataFields = "language"
//        config.vectorStoreThroughput = 400
//
//        val cosmosClient = CosmosClientBuilder()
//            .endpoint("https://neon-cosmosdb.documents.azure.com:443/")
//            .userAgentSuffix("SpringAI-CDBNoSQL-VectorStore")
//            .key("qNR6UUukfcuFYtDYqwrzU0XTxxWYYdesEKofNvZxldB01wjBWeY5PF94iut1g2cjLARVaQVutizcACDbELnU6g")
//            .gatewayMode()
//            .buildAsyncClient()
//
//        return CosmosDBVectorStore(
//            observationRegistry,
//            null,
//            cosmosClient,
//            config,
//            embeddingModel(openAiClient)
//        )
//    }
//
//    fun embeddingModel(openAIClient: OpenAIClient?): EmbeddingModel {
//        val options = AzureOpenAiEmbeddingOptions.builder().withDeploymentName("embedding-model").build()
//        val embeddingModel = AzureOpenAiEmbeddingModel(openAIClient, MetadataMode.EMBED, options)
//        return embeddingModel
//    }
//
//    fun store(contractName: String, content: String, heading: String, pageNumber: Number, version: String?) {
//        val document =
//            Document(UUID.randomUUID().toString(), content, Map.of<String, Any>("contractName",contractName,"heading", heading, "pageNumber", pageNumber, "version", version))
//        vectorStore!!.add(listOf(document))
//    }
//
//    fun search(query: String?, contractName: String?, version: String?): List<String> {
//        val searchRequest = SearchRequest.query(query).withTopK(2)
//        searchRequest.withSimilarityThreshold(0.8)
//       // searchRequest.withFilterExpression("metadata.contractName='$contractName' AND metadata.version='$version'")
//        val results = vectorStore!!.similaritySearch(searchRequest)
//        return results.map { it.content }
//    }
}
