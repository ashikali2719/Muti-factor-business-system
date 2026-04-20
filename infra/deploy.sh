#!/bin/bash

# Zero-Cost Azure Deployment Script
# This script automates the deployment to Azure free tier services

set -e

echo "🚀 Multi-Factor Business System - Azure Free Tier Deployment"
echo "==========================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="multi-factor-biz"
ENVIRONMENT="dev"
LOCATION="southeastasia"
DOCKER_IMAGE="${DOCKER_USERNAME}/multi-factor-backend:latest"

echo -e "${YELLOW}[1/6] Checking prerequisites...${NC}"
command -v az >/dev/null 2>&1 || { echo -e "${RED}Azure CLI not found${NC}"; exit 1; }
command -v docker >/dev/null 2>&1 || { echo -e "${RED}Docker not found${NC}"; exit 1; }
echo -e "${GREEN}✓ Prerequisites OK${NC}"

echo -e "${YELLOW}[2/6] Creating resource group...${NC}"
RESOURCE_GROUP="${APP_NAME}-${ENVIRONMENT}-rg"
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION
echo -e "${GREEN}✓ Resource group created${NC}"

echo -e "${YELLOW}[3/6] Building and pushing Docker image...${NC}"
docker build -t $DOCKER_IMAGE .
docker push $DOCKER_IMAGE
echo -e "${GREEN}✓ Docker image pushed${NC}"

echo -e "${YELLOW}[4/6] Deploying infrastructure (Bicep)...${NC}"
DEPLOYMENT=$(az deployment group create \
  --resource-group $RESOURCE_GROUP \
  --template-file infra/main.bicep \
  --parameters infra/main.bicepparam \
  --query properties.outputs)
echo -e "${GREEN}✓ Infrastructure deployed${NC}"

echo -e "${YELLOW}[5/6] Building frontend...${NC}"
cd project
npm install
npm run build
cd ..
echo -e "${GREEN}✓ Frontend built${NC}"

echo -e "${YELLOW}[6/6] Getting deployment outputs...${NC}"
STATIC_WEB_APP_URL=$(echo $DEPLOYMENT | grep -o '"staticWebAppUrl":{"value":"[^"]*' | cut -d'"' -f4)
CONTAINER_APP_URL=$(echo $DEPLOYMENT | grep -o '"containerAppUrl":{"value":"[^"]*' | cut -d'"' -f4)

echo -e "${GREEN}✓ Deployment complete!${NC}"
echo ""
echo "=========================================================="
echo -e "${GREEN}🎉 Deployment Successful!${NC}"
echo "=========================================================="
echo -e "Frontend URL: ${GREEN}$STATIC_WEB_APP_URL${NC}"
echo -e "Backend URL:  ${GREEN}$CONTAINER_APP_URL${NC}"
echo ""
echo "⚠️  Backend may take 1-2 minutes to start"
echo "💾 Estimated Monthly Cost: \$0.00 ✓"
echo ""
echo "Next steps:"
echo "1. Update project/.env with backend URL"
echo "2. Rebuild frontend: cd project && npm run build"
echo "3. Visit frontend URL in browser"
echo "=========================================================="
