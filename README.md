# Contract Analysis Service

This project is a Spring Boot service for analyzing contract clauses using OpenAI and Azure Cognitive Search.

## Prerequisites

- Java 11 or higher
- Maven
- Azure account with Form Recognizer and OpenAI services

## Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-repo/contract-analysis-service.git
   cd contract-analysis-service
   ```

2. **Configure application properties: Update the application.properties file with your Azure Form Recognizer and OpenAI credentials.**

```spring.ai.azure.openai.endpoint=YOUR_OPENAI_ENDPOINT
spring.ai.azure.openai.api-key=YOUR_OPENAI_API_KEY
spring.ai.azure.openai.chat.options.deployment-name=YOUR_DEPLOYMENT_NAME
azure.search.endpoint=YOUR_SEARCH_ENDPOINT
azure.search.key=YOUR_SEARCH_KEY
azure.formrecognizer.endpoint=YOUR_FORM_RECOGNIZER_ENDPOINT
azure.formrecognizer.key=YOUR_FORM_RECOGNIZER_KEY
```

3. **Build the project:**  
   ```bash
   mvn clean install
   ```  

4. **Run the service:**  
   ```bash  
    mvn spring-boot:run 
    ```

**API Documentation**
1. **Upload Contract**
Endpoint: POST /api/contracts/upload  
Description: Uploads a contract file and indexes its content using Azure Cognitive Search.
```bash
curl -X POST "http://localhost:8080/api/contracts/upload" \
     -F "file=@path/to/contract.pdf" \
     -F "name=SampleContract" \
     -F "version=1.0"
     
```
**Request Parameters:**
- `file (MultipartFile)`: The contract file to be uploaded.
- `name (String)`: The name of the contract.
- `version (String)`: The version of the contract

### Analyze Contract

**Endpoint:** `GET /api/contracts/analyze`

**Description:** Analyzes the clauses of a contract and returns a list of clause analysis responses.

**Request Parameters:**
- `name` (String): The name of the contract.
- `version` (String): The version of the contract.

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/contracts/analyze?name=SampleContract&version=1.0"
```

Response:
```json
[
    {
    "summary": "Summarize the clause here...",
    "risk_score": "High",
    "reason": "Reason for the risk score...",
    "missing_terms": ["term1", "term2"]
    },
    ...
]