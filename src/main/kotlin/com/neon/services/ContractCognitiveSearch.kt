package com.neon.services

import com.azure.core.credential.AzureKeyCredential
import com.azure.search.documents.SearchClientBuilder
import com.azure.search.documents.indexes.SearchIndexClientBuilder
import com.azure.search.documents.indexes.models.IndexDocumentsBatch
import com.azure.search.documents.indexes.models.SearchField
import com.azure.search.documents.indexes.models.SearchFieldDataType
import com.azure.search.documents.indexes.models.SearchIndex
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContractCognitiveSearch {

    @Value("\${azure.search.endpoint}")
    lateinit var searchEndpoint: String

    @Value("\${azure.search.key}")
    lateinit var searchKey: String

    @Value("\${azure.search.semantic-search-config}")
    lateinit var semanticSearchConfig: String



    private val searchIndexClient by lazy {
        SearchIndexClientBuilder().endpoint(searchEndpoint).credential(AzureKeyCredential(searchKey)).buildClient()
    }

    fun indexContract(contractName: String, version: String, paragraphs: Map<String, MutableList<String>>){
        val indexName = createOrUpdateIndex(contractName, version)
        val searchClient = SearchClientBuilder().endpoint(searchEndpoint).indexName(indexName).credential(AzureKeyCredential(searchKey)).buildClient()

        // Index the contract paragraphs
        val docList = mutableListOf<Map<String, String>>()
        paragraphs.forEach() { (key, value) ->
            val document = mapOf(
                "id" to UUID.randomUUID().toString(),
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

    fun contractIndexName(contractName: String, version: String): String {
        return "${contractName}-${version}".lowercase().replace('.', '-')
    }

    fun createOrUpdateIndex(contractName: String, version: String): String {
        val indexName = contractIndexName(contractName, version)
        val index = SearchIndex(indexName).setFields(listOf(
            SearchField("id", SearchFieldDataType.STRING).setKey(true),
            SearchField("name", SearchFieldDataType.STRING),
            SearchField("version", SearchFieldDataType.STRING),
            SearchField("content", SearchFieldDataType.STRING),
        ))
        searchIndexClient.createOrUpdateIndex(index)
        return indexName
    }

}