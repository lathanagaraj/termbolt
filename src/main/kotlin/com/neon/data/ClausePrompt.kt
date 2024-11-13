package com.neon.data

data class ClausePrompt(
    val clause: String,
    val prompt: String,
    val weight: Int
)

fun createClausePrompts(): Map<String, ClausePrompt> {
    return mapOf(
            "payment_terms" to ClausePrompt(
                clause = "payment terms",
                prompt ="""
You are an expert legal assistant trained to analyze contracts.
Task:
1. Given the attached contract data, your goal is to analyze all clauses related to payment terms. If no clauses founds then return an empty JSON object as described below.
2. Output a JSON object with the following fields:
    - "summary": "Summarize the payment terms clauses in a concise manner, highlighting the key obligations or actions involved. If no payment terms are found, state that clearly."
    - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
        - Very High: The clause is missing, or it contains vague language that could lead to disputes.
        - High: The clause is ambiguous, lacks details on payment schedules, amounts, or penalties, or is overly one-sided.
        - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
        - Low: The clause is well-defined, balanced, and includes specific payment schedules, amounts, methods, penalties, and other relevant conditions.
    - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no payment terms are found, explain the implications of this absence."
    - missing_terms: "Identify missing elements (e.g., payment schedule, late fees, interest rates, invoicing procedures) if applicable."
    
** Strict Format: Provide a JSON response like this. No additional commentary or explanations. **

```json
  {
  "summary": "The payment terms specify that the buyer shall pay the seller within 30 days of receiving the invoice. Payments shall be made by bank transfer, and late payments will incur an interest charge of 2% per month. The invoice must be submitted within 10 days after the completion of the service.",
  "risk_score": "Low",
  "reason": "The payment terms are clear, with specific timelines for payment, invoicing procedures, and penalties for late payments.",
  "missing_terms": []
  }
```  
END_OF_JSON


""".trimIndent(),
                weight = 1,

            ),
        "termination_clause" to ClausePrompt(
            clause = "termination clause",
            prompt = """
You are an expert legal assistant trained to analyze contracts.
Task:
1 Given the attached contract data your goal is to analyze all clauses related to termination clause. If no clauses founds then return an empty JSON object as described below.
2 Output JSON object with the following fields:
  - "summary": "Summarize the termination clause clauses in concise manner, highlighting the key obligations or actions involved. If no termination clause found, state that clearly."
  - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
    - Very High: The clause is missing, or it contains vague language that could lead to disputes.
    - High: The clause is ambiguous, lacking details on conditions or consequences, or is overly one-sided.
    - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
    - Low: The clause is well-defined, balanced, and includes specific conditions, notice periods, and consequences for termination.
  - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no termination clause found, explain the implications of this absence."
  - missing_terms: "Identify missing elements (e.g., notice period, termination fees, mutual rights) if applicable."

** Strict Format: Provide a JSON response like this. No additional commentary or explanations **

```json
  {
  "summary": "Either party may terminate this Agreement for convenience, without penalty, by providing a written notice at least 60 days prior to the intended termination date. During this notice period, both parties agree to fulfill all existing obligations, including any outstanding orders and payment commitments. Termination under this clause does not affect any accrued rights or obligations prior to the termination date.",
  "risk_score": "Low",
  "reason": "The termination clause is clear and balanced, with specific notice period and obligations for both parties.",
  "missing_terms": []
  }
```
END_OF_JSON
""".trimIndent(),
            weight = 1
        )
    )
}