package com.neon.data

data class ClausePrompt(
    val clause: String,
    val prompt: String,
    val weight: Int
)

fun createClausePrompts(): Map<String, ClausePrompt> {
    return mapOf(
            "payment_terms" to ClausePrompt(
                clause = "Payment Terms",
                prompt ="""
You are an expert legal assistant trained to analyze contracts.
Task:
1. Given the attached contract data, your goal is to analyze all clauses related to payment terms. If no clauses founds then return an empty JSON object as described below.
2. Output a JSON object with the following fields. It is critical to adhere to this schema in all cases. DO NOT include ```json``` in your response.
    - "summary": "Summarize the payment terms clauses in a concise manner, highlighting the key obligations or actions involved. If no payment terms are found, state that clearly."
    - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
        - Very High: The clause is missing, or it contains vague language that could lead to disputes.
        - High: The clause is ambiguous, lacks details on payment schedules, amounts, or penalties, or is overly one-sided.
        - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
        - Low: The clause is well-defined, balanced, and includes specific payment schedules, amounts, methods, penalties, and other relevant conditions.
    - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no payment terms are found, explain the implications of this absence."
    - missing_terms: "Identify missing elements (e.g., payment schedule, late fees, interest rates, invoicing procedures) if applicable."
    
** Strict Format: Provide a JSON response like this. No additional commentary or explanations. **

  {
  "clause": "Payment Terms",
  "summary": "The payment terms specify that the buyer shall pay the seller within 30 days of receiving the invoice. Payments shall be made by bank transfer, and late payments will incur an interest charge of 2% per month. The invoice must be submitted within 10 days after the completion of the service.",
  "risk_score": "Low",
  "reason": "The payment terms are clear, with specific timelines for payment, invoicing procedures, and penalties for late payments.",
  "missing_terms": []
  }
 
END_OF_JSON


""".trimIndent(),
                weight = 1,

            ) ,
        "termination_clause" to ClausePrompt(
            clause = "Termination Terms",
            prompt = """
You are an expert legal assistant trained to analyze contracts.
Task:
1 Given the attached contract data your goal is to analyze all clauses related to termination clause. If no clauses found then return an empty JSON object as described below.
2 Output JSON object with the following fields. It is critical to adhere to this schema in all cases. DO NOT include ```json``` in your response.
  - "summary": "Summarize the termination clause clauses in concise manner, highlighting the key obligations or actions involved. If no termination clause found, state that clearly."
  - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
    - Very High: The clause is missing, or it contains vague language that could lead to disputes.
    - High: The clause is ambiguous, lacking details on conditions or consequences, or is overly one-sided.
    - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
    - Low: The clause is well-defined, balanced, and includes specific conditions, notice periods, and consequences for termination.
  - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no termination clause found, explain the implications of this absence."
  - missing_terms: "Identify missing elements (e.g., notice period, termination fees, mutual rights) if applicable."

** Strict Format: Provide a JSON response like this. No additional commentary or explanations **

  {
  "clause": "Termination Terms",
  "summary": "Either party may terminate this Agreement for convenience, without penalty, by providing a written notice at least 60 days prior to the intended termination date. During this notice period, both parties agree to fulfill all existing obligations, including any outstanding orders and payment commitments. Termination under this clause does not affect any accrued rights or obligations prior to the termination date.",
  "risk_score": "Low",
  "reason": "The termination clause is clear and balanced, with specific notice period and obligations for both parties.",
  "missing_terms": []
  }
  
END_OF_JSON
""".trimIndent(),
            weight = 1
        ),
        "dispute_resolution_clause" to ClausePrompt(
            clause = "Dispute Resolution",
            prompt = """
You are an expert legal assistant trained to analyze contracts.
Task:
1 Given the attached contract data your goal is to analyze all clauses related to dispute resolution. If no clauses found then return an empty JSON object as described below.
2 Output JSON object with the following fields. It is critical to adhere to this schema in all cases. DO NOT include ```json``` in your response.
  - "summary": "Summarize the dispute resolution clause clauses in concise manner, highlighting the key principles of mediation or arbitration involved. If no dispute resolution clause found, state that clearly."
  - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
    - Very High: The clause is missing, or it contains vague language that could lead to disputes.
    - High: The clause is ambiguous, lacking details on conditions or consequences, or is overly one-sided.
    - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
    - Low: The clause is well-defined, balanced, and includes specific conditions, notice periods, and consequences for dispute resolution.
  - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no dispute resolution clause found, explain the implications of this absence."
  - missing_terms: "Identify missing elements (e.g., arbitration, dispute resolution) if applicable."

** Strict Format: Provide a JSON response like this. No additional commentary or explanations **

  {
  "clause": "Dispute Resolution",
  "summary": "Summary of the dispute resolution clause and any rules governing it",
  "risk_score": "Low",
  "reason": "The dispute resolution clause is clear and balanced.",
  "missing_terms": []
  }

END_OF_JSON
 """.trimIndent(),
            weight = 1
        ),
        "export_control_clause" to ClausePrompt(
            clause = "Export Control Terms",
            prompt =
            """
            You are an expert legal assistant trained to analyze contracts.
            Task:
            1 Given the attached contract data your goal is to analyze all clauses related to export control clause. If no clauses found then return an empty JSON object as described below.
            2 Output JSON object with the following fields. It is critical to adhere to this schema in all cases. DO NOT include ```json``` in your response.
              - "summary": "Summarize the export control clause in concise manner, If no export control clause found, state that clearly. Specifically, look for any missing information or unclear terms that could lead to compliance issues or increased risk. Given any missing details, assign a high risk score to this clause, as it may create legal or regulatory vulnerabilities. Return a detailed summary along with the identified missing points and the assigned risk score at the end of the response. "
              - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
                - Very High: The clause is missing, or it contains vague language that could lead to disputes.
                - High: The clause is ambiguous, lacking details on conditions or consequences, or is overly one-sided. The clause is missing key information like the name of the government authority responsible for issuing export licenses, a list of prohibited entities, such as countries. The clause lacks specifics about when indemnification would occur.
                - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
                - Low: The clause is well-defined, balanced, and includes specific amount or formula for liability cap.
              - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no export control clause found, explain the implications of this absence."
              - missing_terms: "Identify missing elements (e.g. restricted countires) if applicable."

            ** Strict Format: Provide a JSON response like this. No additional commentary or explanations **

              {
              "clause": "Export Control Terms",
              "summary": "The Parties agree to comply with all applicable export control and trade compliance laws and regulations related to the export, re-export, or transfer of goods, technology, or services under this Agreement. The Parties shall not engage in any activities or transactions in violation of applicable laws, nor shall they provide access to controlled items, technologies, or data to individuals or entities prohibited under applicable trade laws.",
              "risk_score": "Low",
              "reason": "The export control clause is clear and balanced, with specific obligations for both parties.",
              "missing_terms": []
              }

            END_OF_JSON
        """.trimIndent(),
            weight = 1
        ),
        "rebates_clause" to ClausePrompt(
            clause = "Rebates and Discounts",
            prompt =
            """
            You are an expert legal assistant trained to analyze contracts.
            Task:
            1 Given the attached contract data your goal is to analyze all clauses related to Calculation and Payment of Rebates/Discounts clause. If no clauses found then return an empty JSON object as described below.
            2 Output JSON object with the following fields. It is critical to adhere to this schema in all cases. DO NOT include ```json``` in your response.
              - "summary": "Summarize the Calculation and Payment of Rebates/Discounts clause in concise manner, If no Calculation and Payment of Rebates/Discounts clause found, state that clearly. Specifically, look for any missing information or unclear terms that could lead to compliance issues or increased risk. Given any missing details, assign a high risk score to this clause, as it may create legal or regulatory vulnerabilities. Return a detailed summary along with the identified missing points and the assigned risk score at the end of the response. Identify 
            any important financial or procedural details, including calculation formulas, timelines for payment, conditions for eligibility, and methods of rebate/discount disbursement. "
              - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
                - Very High: The clause is missing, or it contains vague language that could lead to disputes.
                - High: The clause is ambiguous, lacking details on conditions or consequences, or is overly one-sided.
                - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
                - Low: The clause is well-defined, balanced, and includes specific amount or formula for liability cap.
              - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no Calculation and Payment of Rebates/Discounts clause found, explain the implications of this absence."
              - missing_terms: "Identify missing elements (e.g. restricted countires) if applicable."

            ** Strict Format: Provide a JSON response like this. No additional commentary or explanations **

              {
              "clause": "Calculation and Payment of Rebates/Discounts Terms",
              "summary": "The Parties agree that rebates and/or discounts shall be calculated based on mutually agreed criteria and conditions, and payments shall be made at intervals specified in this Agreement. The calculation of rebates/discounts will be based on relevant sales volumes, purchase commitments, or other factors as applicable. Payments will be issued following the completion of each relevant period, provided that all criteria are met.",
              "risk_score": "Low",
              "reason": "The Calculation and Payment of Rebates/Discounts clause is clear and balanced, with specific obligations for both parties.",
              "missing_terms": []
              }

            END_OF_JSON
        """.trimIndent(),
            weight = 1
        ),
        "indemnity_clause" to ClausePrompt(
            clause = "Indemnity",
            prompt =
            """
            You are an expert legal assistant trained to analyze contracts.
            Task:
            1. Given the attached contract data, your goal is to analyze all clauses related to indemnity. If no clauses are found, then return an empty JSON object as described below.
            2. Output a JSON object with the following fields. It is critical to adhere to this schema in all cases. DO NOT include ```json``` in your response.
              - "summary": "Summarize the indemnity clause in a concise manner, highlighting the key responsibilities and obligations of the parties involved. If no indemnity clause is found, state that clearly."
              - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
                - Very High: The clause is missing, or it contains vague language that could lead to disputes.
                - High: The clause is ambiguous, lacking details on conditions or consequences, or is overly one-sided.
                - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
                - Low: The clause is well-defined, balanced, and includes specific conditions, notice periods, and consequences for indemnity.
              - "reason": "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no indemnity clause is found, explain the implications of this absence."
              - "missing_terms": "Identify missing elements (e.g., specific liabilities, conditions for indemnity) if applicable."
            
            ** Strict Format: Provide a JSON response like this. No additional commentary or explanations **
            
            {
              "clause": "Indemnity",
              "summary": "Summary of the indemnity clause and any rules governing it",
              "risk_score": "Low",
              "reason": "The indemnity clause is clear and balanced.",
              "missing_terms": []
            }
            
            END_OF_JSON
            """.trimIndent(),
            weight = 1
        ),
        "limitation_liability" to ClausePrompt(
          clause = "Limitation of Liability",
          prompt = """
You are an expert legal assistant trained to analyze contracts.
Task:
1 Given the attached contract data your goal is to analyze all clauses related to limitation of liability clause. If no clauses found then return an empty JSON object as described below.
2 Output JSON object with the following fields. It is critical to adhere to this schema in all cases. DO NOT include ```json``` in your response.
  - "summary": "Summarize the limitation of liability clause in concise manner, If no limitation of liability clause found, state that clearly. "
  - "risk_score": "Very High, High, Medium, Low". Assign one single risk score to the clause based on the following criteria:
    - Very High: The clause is missing, or it contains vague language that could lead to disputes, or it is missing key information like specific amount of liability cap or specific circumstance or events that would qualify for exemption under the limitation. It is missing key numerical data like the amount of liability cap and details on handling third party claims like notice requirements and defense obligations. It is missing specific definitions or thresholds of gross negligence or willful misconduct
    - High: The clause is ambiguous, lacking details on conditions or consequences, or is overly one-sided.
    - Medium: The clause is mostly clear but has some minor ambiguities or lacks certain protections.
    - Low: The clause is well-defined, balanced, and includes specific amount or formula for liability cap.
  - reason: "Provide a rationale for the assigned risk rating, explaining why certain elements increase or reduce the risk level. If no limitation of liability clause found, explain the implications of this absence."
  - missing_terms: "Identify missing elements (e.g.,thresholds of gross negligence, willful misconduct) if applicable."

** Strict Format: Provide a JSON response like this. No additional commentary or explanations **

  {
  "clause": "limitation of liability Terms",
  "summary": "Either party may terminate this Agreement for convenience, without penalty, by providing a written notice at least 60 days prior to the intended limitation of liability date. During this notice period, both parties agree to fulfill all existing obligations, including any outstanding orders and payment commitments. limitation of liability under this clause does not affect any accrued rights or obligations prior to the limitation of liability date.",
  "risk_score": "Low",
  "reason": "The limitation of liability clause is clear and balanced, with specific notice period and obligations for both parties.",
  "missing_terms": []
  }
  
END_OF_JSON
""".trimIndent(),
          weight = 1
        ),
    )
}