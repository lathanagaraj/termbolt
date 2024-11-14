package com.neon.services

import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.*
import com.azure.core.credential.AzureKeyCredential
import com.azure.core.http.policy.ExponentialBackoffOptions
import com.azure.core.http.policy.RetryOptions
import com.azure.core.http.policy.RetryPolicy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.neon.data.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ContractClauseAnalysisService {

    @Value("\${spring.ai.azure.openai.endpoint}")
    lateinit var openAiEndpoint: String

    @Value("\${spring.ai.azure.openai.api-key}")
    lateinit var openAiKey: String

    @Value("\${spring.ai.azure.openai.chat.options.deployment-name}")
    lateinit var chatDeploymentModel: String

    @Value("\${azure.search.endpoint}")
    lateinit var searchEndpoint: String

    @Value("\${azure.search.key}")
    lateinit var searchKey: String

    private val exponentialBackoffOptions: ExponentialBackoffOptions = ExponentialBackoffOptions()
            .setMaxRetries(3)
            .setBaseDelay(Duration.ofSeconds(120))
            .setMaxDelay(Duration.ofSeconds(360))

    private val retryOptions: RetryOptions = RetryOptions(exponentialBackoffOptions)
    private val retryPolicy: RetryPolicy = RetryPolicy(retryOptions)


    private val openAiClient by lazy {
        OpenAIClientBuilder().endpoint(openAiEndpoint).credential(AzureKeyCredential(openAiKey)).retryPolicy(retryPolicy).buildClient()
    }


    fun analyzeContract(contractName:String, version: String): MutableList<ClauseAnalysisResponse> {
       val responses = mutableListOf<ClauseAnalysisResponse>()
       createClausePrompts().forEach { (clause ,clausePrompt) ->
          println("Analyzing $clause")
          try{
            val response = analyzeContractClause(contractName, version, clausePrompt)
            responses.add(response)
              Thread.sleep(5000)
          } catch (e: Exception) {
              //TODO: Hack code. Need to fix this retry issues
              println("Error analyzing $clause ${e.message}")
              Thread.sleep(20000)
          }
       }
       return responses
    }

    fun analyzeContractClause(contractName:String, version: String, clausePrompt: ClausePrompt): ClauseAnalysisResponse {
        val indexName = contractIndexName(contractName, version)
        val prompt =  clausePrompt.prompt
        val messages = listOf(
            ChatRequestSystemMessage( prompt),
            ChatRequestUserMessage("analyze ${clausePrompt.clause}")
        )

        val datasource =  AzureSearchChatExtensionConfiguration(AzureSearchChatExtensionParameters(searchEndpoint,indexName).setAuthentication(OnYourDataApiKeyAuthenticationOptions(searchKey)))

        val options = ChatCompletionsOptions(messages)
            .setModel(chatDeploymentModel)
            .setMaxTokens(1000)
            .setTemperature(0.0)
            .setTopP(1.0)
            .setStop(listOf("END_OF_JSON"))
            .setFrequencyPenalty(0.0)
            .setPresencePenalty(0.0)
            .setDataSources(listOf(datasource))


        println("Sending request to OpenAI with prompt ------------------------------------ ")
        println("$prompt")
        val response = openAiClient.getChatCompletions(chatDeploymentModel,options)

        val jsonResponse = response.choices[0].message.content.trim()
        println("got response from OpenAI ------------------------------------ ")
        println("$jsonResponse")
        return parseResponse(jsonResponse)
    }




    fun parseResponse(response: String): ClauseAnalysisResponse {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(response, ClauseAnalysisResponse::class.java)
    }
    fun contractIndexName(contractName: String, version: String): String {
        return "${contractName}-${version}".lowercase().replace('.', '-')
    }

}