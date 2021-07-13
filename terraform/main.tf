# Configure the Azure provider
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 2.65"
    }
  }

  required_version = ">= 0.14.9"
}

provider "azurerm" {
  features {}
}

locals {
  resource_group_name   = "myK8sResourceGroup"
  resource_group_location   = "eastus"
}

resource "azurerm_container_registry" "acr" {
  name                     = "myK8sContainerRegistry"
  resource_group_name      = local.resource_group_name
  location                 = local.resource_group_location
  sku                      = "Basic"
  admin_enabled            = false
}

resource "azurerm_kubernetes_cluster" "akc" {
  name                = "myK8sCluster"
  resource_group_name = local.resource_group_name
  location            = local.resource_group_location
  dns_prefix          = "myK8sCluster"

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
