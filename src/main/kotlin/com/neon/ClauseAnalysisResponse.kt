package com.neon

data class ClauseAnalysisResponse(
    val clause: String,
    val presence: Boolean,
    val riskScore: Double,
    val citation: String,
    val summary: String)
