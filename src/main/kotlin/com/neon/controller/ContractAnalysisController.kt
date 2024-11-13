package com.neon.controller;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.core.credential.AzureKeyCredential
import com.azure.core.util.BinaryData
import com.neon.data.ClauseAnalysisResponse
import com.neon.services.ContractClauseAnalysisService
import com.neon.services.ContractCognitiveSearch
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


    private val formRecognizerClient by lazy {
        DocumentAnalysisClientBuilder().endpoint(formRecognizerEndpoint)
            .credential(AzureKeyCredential(formRecognizerKey)).buildClient()
    }

    @Autowired
    lateinit var contractClauseAnalysisService: ContractClauseAnalysisService

    @Autowired
    lateinit var contractCognitiveSearch: ContractCognitiveSearch

    @PostMapping("/upload")
    fun uploadContract(@RequestParam("file") file: MultipartFile, @RequestParam("name") name: String, @RequestParam("version") version: String) {
        val content = file.bytes
        val paragraphs = extractText(content)
        contractCognitiveSearch.indexContract(name, version, paragraphs)
    }


    @GetMapping("/analyze")
    fun analyzeContract(@RequestParam name: String, @RequestParam version: String): MutableList<ClauseAnalysisResponse> {
        return contractClauseAnalysisService.analyzeContract(name, version)
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


}



