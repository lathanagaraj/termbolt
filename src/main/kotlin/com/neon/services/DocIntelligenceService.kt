package com.neon.services

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder
import com.azure.core.credential.AzureKeyCredential
import com.azure.core.util.BinaryData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DocIntelligenceService {

    @Value("\${azure.formrecognizer.endpoint}")
    lateinit var formRecognizerEndpoint: String

    @Value("\${azure.formrecognizer.key}")
    lateinit var formRecognizerKey: String


    private val formRecognizerClient by lazy {
        DocumentAnalysisClientBuilder().endpoint(formRecognizerEndpoint)
            .credential(AzureKeyCredential(formRecognizerKey)).buildClient()
    }

    fun extractText(content: ByteArray): Map<String, MutableList<String>> {
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

//        val tables = mutableListOf<List<Map<String, Any>>>()
//        if (result.tables != null) {
//            var tableCounter = 0
//            result.tables.forEach { table ->
//                val tableData = mutableListOf<Map<String, Any>>()
//                table.cells.forEach { cell ->
//                    tableData.add(
//                        mapOf(
//                            "row" to cell.rowIndex,
//                            "column" to cell.columnIndex,
//                            "text" to cell.content
//                        )
//                    )
//                }
//                tableCounter++
//                headingsAndParagraphs["Tables-$tableCounter"] = mutableListOf(tableData.joinToString { it.toString() })
//            }
//        }


        if (result.tables != null) {
            var tableCounter = 0
            result.tables.forEach { table ->
                val headers = table.cells.filter { it.rowIndex == 0 }.map { it.content }
                val rows = mutableMapOf<Int, MutableMap<String, Any>>()

                table.cells.forEach { cell ->
                    val rowIndex = cell.rowIndex
                    val columnIndex = cell.columnIndex
                    val header = headers[columnIndex]

                    if (!rows.containsKey(rowIndex)) {
                        rows[rowIndex] = mutableMapOf()
                    }

                    rows[rowIndex]!![header] = cell.content
                }

                tableCounter++
                headingsAndParagraphs["Tables-$tableCounter"] = rows.values.map { it.toString() }.toMutableList()
            }
        }
        return headingsAndParagraphs;
    }

}