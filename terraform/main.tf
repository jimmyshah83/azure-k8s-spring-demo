# Configure the Azure provider
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 2.65"
    }
  }

  backend "azurerm" {
    resource_group_name  = "k8sResourceGroup"
    storage_account_name = "tfk8ssa"
    container_name       = "tfsacontainer"
    key                  = "marketdata.tfstate"
  }

  required_version = ">= 0.14.9"
}

provider "azurerm" {
  features {}
}

locals {
  resource_group_name   = "k8sResourceGroup"
  resource_group_location   = "eastus"
  marketdata_api_spec = "../apim/api-spec.json"
}

resource "azurerm_container_registry" "acr" {
  name                     = "k8smarketdataregistry"
  resource_group_name      = local.resource_group_name
  location                 = local.resource_group_location
  sku                      = "Basic"
  admin_enabled            = false
}

resource "azurerm_kubernetes_cluster" "akc" {
  name                = "marketdatacluster"
  resource_group_name = local.resource_group_name
  location            = local.resource_group_location
  dns_prefix          = "marketdatacluster"

  # 2 vcpus, 4 GiB memory
  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "Standard_B2s"
  }

  identity {
    type = "SystemAssigned"
  }

  tags = {
    Environment = "Development"
  }
}

output "client_certificate" {
  value = azurerm_kubernetes_cluster.akc.kube_config.0.client_certificate
}

output "kube_config" {
  sensitive = true
  value = azurerm_kubernetes_cluster.akc.kube_config_raw
}

resource "azurerm_api_management" "apim" {
  name                = "k8s-apim-service"
  location            = local.resource_group_location
  resource_group_name = local.resource_group_name
  publisher_name      = "Jimmy Shah"
  publisher_email     = "jimmyshah83@gmail.com"

  sku_name = "Developer_1"
}

resource "azurerm_api_management_api" "marketdata-api" {
  name                = "marketdata-api"
  resource_group_name = local.resource_group_name
  api_management_name = azurerm_api_management.apim.name
  revision            = "1"
  display_name        = "Marketdata API"
  protocols           = ["https"]

  import {
    content_format = "openapi"
    content_value  = "http://52.188.131.153/v3/api-docs"
  }
  path = ""
}
