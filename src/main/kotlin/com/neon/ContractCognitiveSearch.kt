package com.neon

import com.azure.core.credential.AzureKeyCredential
import com.azure.core.util.Context
import com.azure.search.documents.SearchClientBuilder
import com.azure.search.documents.indexes.models.IndexDocumentsBatch
import com.azure.search.documents.models.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContractCognitiveSearch {

    @Value("\${azure.search.endpoint}")
    lateinit var searchEndpoint: String

    @Value("\${azure.search.key}")
    lateinit var searchKey: String

    @Value("\${azure.search.indexname}")
    lateinit var indexName: String


    @Value("\${azure.search.semantic-search-config}")
    lateinit var semanticSearchConfig: String

    private val searchClient by lazy {
        SearchClientBuilder().endpoint(searchEndpoint).indexName(indexName).credential(AzureKeyCredential(searchKey)).buildClient()
    }


    fun indexContract(contractName: String, version: String, paragraphs: Map<String, MutableList<String>>){
        // Index the contract paragraphs
        val docList = mutableListOf<Map<String, String>>()
        paragraphs.forEach() { (key, value) ->
            val document = mapOf(
                "id" to UUID.randomUUID().toString(),
                "heading" to key,
                "name" to contractName,
                "version" to version,
                "content" to value.joinToString(" ")
            )
           docList.add(document)

        }
        val batchDocsToIndex = IndexDocumentsBatch<Map<String, String>>()
        batchDocsToIndex.addMergeOrUploadActions(docList)
        searchClient.indexDocuments(batchDocsToIndex)
    }

    fun searchContracts(query: String): List<SearchResult> {
        var semanticSearchOptions = SemanticSearchOptions().setSemanticConfigurationName(semanticSearchConfig).setSemanticQuery(query)
        val searchOptions = SearchOptions().setSemanticSearchOptions(semanticSearchOptions).setSearchFields("content").setTop(5)


        val searchPagedIterable = searchClient.search(query, searchOptions, Context.NONE)

        return searchPagedIterable.iterableByPage().flatMap { it.elements }

    }
}