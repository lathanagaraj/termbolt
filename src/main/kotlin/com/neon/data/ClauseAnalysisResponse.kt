package com.neon.data


data class ClauseAnalysisResponse(
    val summary: String,
    val risk_score: String,
    val reason: String,
    val missing_terms: List<String>,
)
