package com.neon;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.ChatCompletionsOptions
import com.azure.ai.openai.models.ChatRequestSystemMessage
import com.azure.ai.openai.models.ChatRequestUserMessage
import com.azure.core.credential.AzureKeyCredential
import com.azure.core.util.BinaryData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/contracts")
class ContractAnalysisController {

    @Value("\${azure.formrecognizer.endpoint}")
    lateinit var formRecognizerEndpoint: String

    @Value("\${azure.formrecognizer.key}")
    lateinit var formRecognizerKey: String


    @Value("\${spring.ai.azure.openai.endpoint}")
    lateinit var openAiEndpoint: String

    @Value("\${spring.ai.azure.openai.api-key}")
    lateinit var openAiKey: String

    @Value("\${spring.ai.azure.openai.chat.options.deployment-name}")
    lateinit var chatDeploymentModel: String



    private val formRecognizerClient by lazy {
        DocumentAnalysisClientBuilder().endpoint(formRecognizerEndpoint)
            .credential(AzureKeyCredential(formRecognizerKey)).buildClient()
    }


    private val openAiClient by lazy {
        OpenAIClientBuilder().endpoint(openAiEndpoint).credential(AzureKeyCredential(openAiKey)).buildClient()
    }

    @Autowired
    lateinit var contractEmbeddingService: ContractEmbeddingService

    @Autowired
    lateinit var contractCognitiveSearch: ContractCognitiveSearch

    @PostMapping("/upload")
    fun uploadContract(@RequestParam("file") file: MultipartFile, @RequestParam("name") name: String, @RequestParam("version") version: String) {
        val content = file.bytes
        val paragraphs = extractText(content)
        embedContract(paragraphs, name, version)
        contractCognitiveSearch.indexContract(name, version, paragraphs)
    }

    fun embedContract(paragraphs: Map<String, MutableList<String>>, name: String, version: String) {
        paragraphs.forEach() { (heading, paragraph) ->
            val fullText = paragraph.joinToString(" ")
            println("Storing $heading")
            println("Storing $fullText")
            try {
                contractEmbeddingService.store("$name-$version", fullText, heading, 1, version)
            }catch (Exception: Exception) {
                println("Error storing $heading")
            }
            println("--------------------------------------------------------------------------------------")
        }
    }



    @GetMapping("/analyze")
    fun analyzeContract(@RequestParam name: String, @RequestParam version: String): List<ClauseAnalysisResponse> {
        val requiredClauses = listOf("Bollywood","Termination", "Confidentiality", "Indemnity")
        var results = mutableListOf<ClauseAnalysisResponse>()
        requiredClauses.map { clause ->
            val exampleText = getClauseText(clause)
            val searchContracts = contractEmbeddingService.search(exampleText, "$name-$version", version)
            val summary = generateSummary(clause, searchContracts.joinToString(" "))
            results.add(ClauseAnalysisResponse(clause, searchContracts.isNotEmpty(), calculateRiskScore(searchContracts), searchContracts.joinToString(" "), summary))
            //val searchContracts = contractCognitiveSearch.searchContracts(exampleText)

        }
        return results

    }

    private fun calculateRiskScore(searchContracts: List<String>): Double {
        //TODO("Not yet implemented")
        return 0.0
    }


    private fun extractText(content: ByteArray): Map<String, MutableList<String>> {
        val poller = formRecognizerClient.beginAnalyzeDocument("prebuilt-document", BinaryData.fromBytes(content))
        val result = poller.finalResult
        val headingsAndParagraphs = mutableMapOf<String, MutableList<String>>()
        var currentHeading = "NOHEADING" // Default heading if no heading is found initially

        result.paragraphs.forEach { paragraph ->
            if (paragraph.role!=null) {
                currentHeading = paragraph.content
                headingsAndParagraphs[currentHeading] = mutableListOf()
            } else {
                headingsAndParagraphs.getOrPut(currentHeading) { mutableListOf() }.add(paragraph.content)
            }
        }
        return headingsAndParagraphs;
    }



    private fun getClauseText(clause: String) = when (clause) {
        "Bollywood" -> "Bollywood is the Indian Hindi-language film industry based in Mumbai."
        "Payment Terms" -> "The payment terms are as follows: 50% upfront and 50% upon completion."
        "Termination" -> "The agreement may be terminated by either party under certain conditions."
        "Confidentiality" -> "Both parties shall keep the terms and conditions of this agreement confidential."
        "Indemnity" -> "One party agrees to indemnify and hold the other harmless against certain claims."

        else -> ""
    }



    fun generateSummary(clauseTitle: String, relevantClauses: String): String {
        val prompt = "Summarize the information about '$clauseTitle' using the following references: $relevantClauses. If the information doesn't cover the clause title, indicate that it is absent."
        val messages = listOf(
            ChatRequestSystemMessage( "You are a contract summarization assistant."),
            ChatRequestUserMessage(prompt)
        )

        val options = ChatCompletionsOptions(messages)
            .setModel("gpt-35-turbo")
            .setMaxTokens(150)

        val response = openAiClient.getChatCompletions(chatDeploymentModel,options)
        return response.choices[0].message.content.trim()
    }
}



