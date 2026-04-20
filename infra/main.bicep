param location string = 'southeastasia'
param appName string = 'multi-factor-biz'
param environment string = 'dev'

var resourceGroupName = '${appName}-${environment}-rg'
var containerRegistryName = '${replace(appName, '-', '')}acr${environment}'
var containerAppName = '${appName}-backend-${environment}'
var staticWebAppName = '${appName}-frontend-${environment}'
var storageAccountName = '${replace(appName, '-', '')}sa${environment}'
var containerAppEnvName = '${appName}-env-${environment}'

// Resource Group
resource resourceGroup 'Microsoft.Resources/resourceGroups@2021-04-01' = {
  name: resourceGroupName
  location: location
}

// Storage Account for data files (Products.csv)
resource storageAccount 'Microsoft.Storage/storageAccounts@2021-06-01' = {
  parent: resourceGroup
  name: storageAccountName
  location: location
  kind: 'StorageV2'
  sku: {
    name: 'Standard_LRS'  // Locally redundant, cheapest option
  }
  properties: {
    accessTier: 'Hot'
    minimumTlsVersion: 'TLS1_2'
  }
}

// Blob container for data
resource blobService 'Microsoft.Storage/storageAccounts/blobServices@2021-06-01' = {
  parent: storageAccount
  name: 'default'
}

resource dataContainer 'Microsoft.Storage/storageAccounts/blobServices/containers@2021-06-01' = {
  parent: blobService
  name: 'data'
  properties: {
    publicAccess: 'None'
  }
}

// Container Registry (optional - for free tier, skip and use Docker Hub)
// resource containerRegistry 'Microsoft.ContainerRegistry/registries@2021-09-01' = {
//   parent: resourceGroup
//   name: containerRegistryName
//   location: location
//   sku: {
//     name: 'Basic'  // Cheapest tier, but costs ~$10/month
//   }
//   properties: {
//     adminUserEnabled: true
//   }
// }

// Container App Environment (consumption plan = pay-per-use, scales to 0)
resource containerAppEnv 'Microsoft.App/managedEnvironments@2023-05-01' = {
  parent: resourceGroup
  name: containerAppEnvName
  location: location
  sku: {
    name: 'Consumption'  // FREE - pay only for execution
  }
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
    }
  }
}

// Container App for Java backend
resource containerApp 'Microsoft.App/containerApps@2023-05-01' = {
  parent: resourceGroup
  name: containerAppName
  location: location
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    managedEnvironmentId: containerAppEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8090
        transport: 'http'
        corsPolicy: {
          allowedOrigins: ['*']  // Allow frontend to call
          allowedMethods: ['GET', 'POST', 'OPTIONS', 'PUT', 'DELETE']
          allowedHeaders: ['*']
          exposeHeaders: ['*']
        }
      }
      secrets: [
        {
          name: 'registry-password'
          value: ''  // Leave empty for Docker Hub public images
        }
      ]
      registries: [
        {
          server: 'docker.io'
          username: ''  // Not needed for public images
          passwordSecretRef: 'registry-password'
        }
      ]
    }
    template: {
      containers: [
        {
          image: 'docker.io/library/openjdk:17-jdk-alpine'  // Placeholder - replace with your image
          name: 'java-backend'
          resources: {
            cpu: '0.25'      // Minimum CPU (4 times per vCPU per month on consumption)
            memory: '0.5Gi'  // 512MB - minimum for Java
          }
          ports: [
            {
              containerPort: 8090
              protocol: 'tcp'
            }
          ]
          env: [
            {
              name: 'JAVA_TOOL_OPTIONS'
              value: '-Xmx256m -Xms128m'  // Limit JVM memory
            }
            {
              name: 'PORT'
              value: '8090'
            }
          ]
        }
      ]
      scale: {
        minReplicas: 0          // Scale to 0 when idle = FREE
        maxReplicas: 1          // Max 1 replica (avoid burst costs)
        rules: [
          {
            name: 'http-rule'
            http: {
              metadata: {
                concurrentRequests: '10'
              }
            }
          }
        ]
      }
    }
  }
}

// Static Web App for frontend (FREE TIER)
resource staticWebApp 'Microsoft.Web/staticSites@2022-03-01' = {
  parent: resourceGroup
  name: staticWebAppName
  location: location
  sku: {
    name: 'Free'    // 100% FREE
    tier: 'Free'
  }
  properties: {
    repositoryUrl: ''  // Set during deployment
    branch: 'main'
    buildProperties: {
      appLocation: 'project'
      outputLocation: 'project/dist'
      appBuildCommand: 'npm run build'
      skipGithubActionWorkflowGeneration: false
    }
  }
}

// Add backend API link to frontend (for routing)
resource staticWebAppApiBackend 'Microsoft.Web/staticSites/config@2022-03-01' = {
  parent: staticWebApp
  name: 'appsettings'
  properties: {
    REACT_APP_BACKEND_URL: containerApp.properties.configuration.ingress.fqdn != null 
      ? 'https://${containerApp.properties.configuration.ingress.fqdn}'
      : 'http://localhost:8090'
  }
}

// Outputs
output staticWebAppUrl string = 'https://${staticWebApp.properties.defaultHostname}'
output containerAppUrl string = containerApp.properties.configuration.ingress != null 
  ? 'https://${containerApp.properties.configuration.ingress.fqdn}'
  : 'http://localhost:8090'
output storageAccountName string = storageAccount.name
output resourceGroupName string = resourceGroup.name
