
provider "azurerm" {
  features {}

  # Subscription ID for Azure subscription 1
  subscription_id = "91da8d06-e6af-41be-b465-64994ef7eeda"
  # client_id       = "your_client_id"
  # client_secret   = "your_client_secret"
  # tenant_id for Default Directory
  tenant_id       = "14959973-6dc1-48e6-90d0-4fe0b15e2cec"
}

# Create a new resource group for everything required for the hack
resource "azurerm_resource_group" "neon-genaihack" {
  name     = "neon-resources"
  location = "East US 2"
}


# Create a new cognitive account for all the Open AI Services
resource "azurerm_cognitive_account" "neon-account" {
  name                = "neon-account-2"
  location            = azurerm_resource_group.neon-genaihack.location
  resource_group_name = azurerm_resource_group.neon-genaihack.name
  kind                = "OpenAI"

  sku_name = "S0"

  tags = {
    Acceptance = "Hack"
  }
}

# Create a new deployment for GPT-4
resource "azurerm_cognitive_deployment" "neon-gpt-4" {
  name                 = "neon-gpt-4"
  cognitive_account_id = azurerm_cognitive_account.neon-account.id
  model {
    format  = "OpenAI"
    name    = "gpt-4"
    version = "turbo-2024-04-09"
  }

  sku {
    name = "GlobalStandard"
  }
}

#Create a new deployment for text-embedding-ada-002
resource "azurerm_cognitive_deployment" "embedding-model" {
  name                 = "embedding-model"
  cognitive_account_id = azurerm_cognitive_account.neon-account.id
  model {
    format  = "OpenAI"
    name    = "text-embedding-ada-002"
    version = "2"
  }

  sku {
    name = "Standard"
  }
}



#Create cosmosdb account
resource "azurerm_cosmosdb_account" "neon-cosmosdb" {
  name                = "neon-cosmosdb"
  location            = azurerm_resource_group.neon-genaihack.location
  resource_group_name = azurerm_resource_group.neon-genaihack.name
  offer_type          = "Standard"
  kind                = "GlobalDocumentDB"

  consistency_policy {
    consistency_level = "Session"
  }

  capabilities {
    name = "EnableNoSQLVectorSearch" # Enables vector search capability
  }

  geo_location {
    location          = azurerm_resource_group.neon-genaihack.location
    failover_priority = 0
  }
}

#Create cosmosdb database
resource "azurerm_cosmosdb_sql_database" "neon-cosmosdb-database" {
  name                = "neon-cosmosdb-database"
  resource_group_name = azurerm_resource_group.neon-genaihack.name
  account_name        = azurerm_cosmosdb_account.neon-cosmosdb.name
}

#Create cosmosdb container
resource "azurerm_cosmosdb_sql_container" "neon-cosmosdb-container" {
  name                = "neon-cosmosdb-container"
  resource_group_name = azurerm_resource_group.neon-genaihack.name
  account_name        = azurerm_cosmosdb_account.neon-cosmosdb.name
  database_name       = azurerm_cosmosdb_sql_database.neon-cosmosdb-database.name
  partition_key_paths  = ["/id"]
  throughput = 800
  indexing_policy {
    indexing_mode = "consistent"
    included_path {
      path = "/metadata/*"
    }
    excluded_path {
      path = "/*"
    }
  }


}

# Output the CosmosDB endpoint and primary key
output "cosmosdb_endpoint" {
  value = azurerm_cosmosdb_account.neon-cosmosdb.endpoint
}

output "cosmosdb_primary_key" {
  value     = azurerm_cosmosdb_account.neon-cosmosdb.primary_key
  sensitive = true
}


# Create a Form Recognizer resource
resource "azurerm_cognitive_account" "neon-document-intelligence" {
  name                = "neon-document-intelligence"
  location            = azurerm_resource_group.neon-genaihack.location
  resource_group_name = azurerm_resource_group.neon-genaihack.name
  kind                = "FormRecognizer"       # Specify the kind of cognitive service
  sku_name            = "S0"        # Use "S0" or "S1" for standard SKUs

  # Optional: Tag your resource for identification
  tags = {
    Environment = "Development"
    Project     = "DocumentProcessing"
  }
}

# Output the endpoint and keys
output "form_recognizer_endpoint" {
  value = azurerm_cognitive_account.neon-document-intelligence.endpoint
}

output "form_recognizer_key" {
  value     = azurerm_cognitive_account.neon-document-intelligence.primary_access_key
  sensitive = true
}


# Create Azure Cognitive Search Service
resource "azurerm_search_service" "neon-search-service" {
  name                = "neon-search-service"
  location            = azurerm_resource_group.neon-genaihack.location
  resource_group_name = azurerm_resource_group.neon-genaihack.name
  sku                 = "standard"
  replica_count       = 1
  partition_count     = 1
  # Optional tags for the resource

}

# Output the Search Service endpoint
output "search_service_key" {
  value       = azurerm_search_service.neon-search-service.primary_key
  sensitive = true
}

# Terraform does not support creating indexes at this point in time
# resource "azurerm_search_index" "neon-contract-index" {
#   name                = "neon-contract-index"
#   resource_group_name = azurerm_resource_group.neon-genaihack.name
#   search_service_name = azurerm_search_service.neon-search-service.name
#
#   field {
#     name     = "id"
#     type     = "Edm.String"
#     key      = true
#     retrievable = true
#   }
#
#   field {
#     name     = "name"
#     type     = "Edm.String"
#     retrievable = true
#   }
#
#   field {
#     name     = "content"
#     type     = "Edm.String"
#     retrievable = true
#   }
#   field {
#     name     = "version"
#     type     = "Edm.String"
#     retrievable = true
#   }
#   field {
#     name     = "heading"
#     type     = "Edm.String"
#     retrievable = true
#   }
# }


# Use this Json to create index from Azure portal on the search servcice
# {
# "name": "neon-contract-index",
# "fields": [
# {
# "name": "id",
# "type": "Edm.String",
# "key": true,
# "retrievable": true,
# "searchable": true
# },
# {
# "name": "content",
# "type": "Edm.String",
# "key": true,
# "retrievable": true,
# "searchable": true
# },
# {
# "name": "heading",
# "type": "Edm.String",
# "key": true,
# "retrievable": true,
# "searchable": true
# },
# {
# "name": "name",
# "type": "Edm.String",
# "key": true,
# "retrievable": true,
# "searchable": true
# },
# {
# "name": "version",
# "type": "Edm.String",
# "key": true,
# "retrievable": true,
# "searchable": true
# }
# ]
# }