package com.neon.controller;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.core.credential.AzureKeyCredential
import com.azure.core.util.BinaryData
import com.neon.data.ClauseAnalysisResponse
import com.neon.services.ContractClauseAnalysisService
import com.neon.services.ContractCognitiveSearch
import com.neon.services.DocIntelligenceService
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

    @Autowired
    lateinit var contractClauseAnalysisService: ContractClauseAnalysisService

    @Autowired
    lateinit var contractCognitiveSearch: ContractCognitiveSearch

    @Autowired
    lateinit var docIntelligenceService: DocIntelligenceService

    @PostMapping("/upload")
    fun uploadContract(@RequestParam("file") file: MultipartFile, @RequestParam("name") name: String, @RequestParam("version") version: String) {
        val content = file.bytes
        val paragraphs = docIntelligenceService.extractText(content)
        contractCognitiveSearch.indexContract(name, version, paragraphs)
    }


    @GetMapping("/analyze")
    fun analyzeContract(@RequestParam name: String, @RequestParam version: String): MutableList<ClauseAnalysisResponse> {
        return contractClauseAnalysisService.analyzeContract(name, version)
    }


}



