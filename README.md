# Deploying applications to AKS
A sample project to demonstrate deploying a containerized application to Azure Kubernetes Service and using Terraform to manage the infrastructure.

#### Azure Resources
<u>Resource Group</u>: Logical grouping of a collection Azure resources \
<u>Container Registry</u>: Build, store, and manage container images \
<u>Kubernetes Cluster</u>: Kubernetes on Azure

#### Terraform Resources
<u>Terraform</u>: Terraform is a tool for building, changing, and versioning infrastructure safely and efficiently. \
Terraform has a [registry](https://registry.terraform.io/) that provides plugins that implement resource types for Azure. We use: \
1. azurerm_resource_group: to create a resource group (RG)
2. azurerm_container_registry: to create Azure container registry (ACR)
2. azurerm_kubernetes_cluster: to create Azure Kubernetes cluster (AKS)

#### CI/CD
Continuous integration and deployment has been implemented using Github actions however, one can use any CI/CD platform for this. \
Please note: API Key secret has been manually created in Kubernetes cluster

#### Deploying application manually
1. Build an image using `mvn spring-boot:build-image`
2. Deploy resources to Azure using terraform  
   `az login` login to Azure with your credentials \
   `cd terraform` \
   `tf init` to initialize terraform \
   `tf plan` to identify any issues with terraform deployment
   `tf apply` to deploy your infrastructure to Azure
3. Deploy the image (marketdata) to Azure container service 
   1. Login to ACR \
      `az acr login --name <RG>`
   2. Fetch ACR login server name to be used to tag the image \
      `az acr list --resource-group <RG> --query "[].{acrLoginServer:loginServer}" --output table`
   3. TAG image with login server address of ACR. Example: \
      `docker tag marketdata:0.0.1-SNAPSHOT <Azure Login Server name>/marketdata:v1`
   4. Push image to ACR \
    `docker push <Azure Login Server name>/marketdata:v1`
4. Deploy the image from ACR to AKC \      
   1. Make sure AKC has access to ACR. If not, run the below command \
      `az aks update --name <Cluster name> --resource-group <RG> --attach-acr <ACR name>`
   2. Configure kubelet to talk to your cluster
      `az aks get-credentials --resource-group <RG> --name <Cluster name>`
   3. Deploy from ACR to AKC using `kubectl`
