package com.neon.services

import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.*
import com.azure.core.credential.AzureKeyCredential
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ContractChatService {
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

    private val openAiClient by lazy {
        OpenAIClientBuilder().endpoint(openAiEndpoint).credential(AzureKeyCredential(openAiKey)).buildClient()
    }

    private val chatHistories = LinkedHashMap<String, List<ContractChat>>()

    data class ContractChat(
        val userMessage: String,
        val assistantMessage: String
    )

    fun chat(contractName: String, version: String, userPrompt: String ): String {
        val indexName = contractIndexName(contractName, version)
        val systemPrompt =  """
        You are an expert legal assistant trained to analyze contracts. 
        Given the attached contract data, your task is to assist users by providing detailed and accurate analysis for questions asked about the contract.
        Do not provide any information or answer any questions that are not directly related to the contract. If a question is outside the scope of the contract, respond with "I can only provide information related to the contract."        
        """.trimIndent()

        val chatKey = "$contractName-$version"
        val chatHistory = chatHistories.getOrDefault(chatKey, mutableListOf())

        val messages = mutableListOf(
            ChatRequestSystemMessage(systemPrompt),
            ChatRequestUserMessage(userPrompt)
        )
        chatHistory.forEach { chat ->
            messages.add(ChatRequestAssistantMessage(chat.userMessage))
            messages.add(ChatRequestAssistantMessage(chat.assistantMessage))
        }

        val datasource =  AzureSearchChatExtensionConfiguration(
            AzureSearchChatExtensionParameters(searchEndpoint,indexName).setAuthentication(
                OnYourDataApiKeyAuthenticationOptions(searchKey)
            ))

        val options = ChatCompletionsOptions(messages)
            .setModel(chatDeploymentModel)
            .setMaxTokens(1000)
            .setTemperature(0.5)
            .setTopP(0.5)
            .setStop(listOf("END_OF_JSON"))
            .setFrequencyPenalty(0.0)
            .setPresencePenalty(0.0)
            .setDataSources(listOf(datasource))



        println("Sending request to OpenAI with prompt ------------------------------------ ")
        println("system prompt : $systemPrompt")
        println("user prompt : $userPrompt")
        val response = openAiClient.getChatCompletions(chatDeploymentModel,options)

        val answer =  response.choices[0].message.content.trim()
        println("got response from OpenAI ------------------------------------ ")

        // Add to chat history
        chatHistories[chatKey] = chatHistory + ContractChat(userPrompt, answer)


        return answer

    }

    fun contractIndexName(contractName: String, version: String): String {
        return "${contractName}-${version}".lowercase().replace('.', '-')
    }



}