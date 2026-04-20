# Zero-Cost Azure Deployment Guide

This guide deploys your Multi-Factor Business System to Azure using **completely free services**.

## 📊 Cost Breakdown

| Service | Free Tier Limit | Estimated Cost |
|---------|-----------------|-----------------|
| Static Web Apps | 1 app/subscription | **$0** ✓ |
| Container Apps (Consumption) | Pay per execution | **$0** (scales to 0) ✓ |
| Storage Account | 100 GB/month | **$0** (under limit) ✓ |
| Total Monthly Cost | — | **$0** ✓ |

**Important Notes:**
- First 50 GB storage is free
- Container Apps charges only during execution (~$0.000278/GB/second)
- At scale 0 with minimal requests, cost is negligible
- Budget alerts included to prevent overages

---

## 🚀 Prerequisites

### 1. Install Azure CLI
```bash
# Windows PowerShell
winget install Microsoft.AzureCLI
# or download from https://aka.ms/installazurecliwindows

# Verify installation
az --version
```

### 2. Login to Azure
```bash
az login
# Opens browser to authenticate
```

### 3. Set Default Subscription
```bash
# List subscriptions
az account list --output table

# Set default (replace with your subscription ID)
az account set --subscription "YOUR_SUBSCRIPTION_ID"
```

### 4. Install Docker
- Download: https://www.docker.com/products/docker-desktop
- Verify: `docker --version`

---

## 📝 Step 1: Prepare Frontend

```bash
cd project

# Install dependencies
npm install

# Build for production
npm run build

# Verify dist folder was created
ls dist/
```

---

## 🐳 Step 2: Build and Push Backend Image

### Option A: Use Docker Hub (Recommended for Free Tier)

```bash
# Create Docker Hub account at https://hub.docker.com

# Login to Docker
docker login -u YOUR_DOCKER_USERNAME

# Build image
docker build -t YOUR_DOCKER_USERNAME/multi-factor-backend:latest .

# Push to Docker Hub
docker push YOUR_DOCKER_USERNAME/multi-factor-backend:latest
```

### Option B: Use Azure Container Registry (Costs ~$10/month)
❌ **Skip this - will cost money. Use Docker Hub instead.**

---

## ☁️ Step 3: Deploy to Azure

### 1. Create Resource Group
```bash
az group create \
  --name multi-factor-biz-dev-rg \
  --location southeastasia
```

### 2. Deploy Infrastructure (Bicep)
```bash
az deployment group create \
  --resource-group multi-factor-biz-dev-rg \
  --template-file infra/main.bicep \
  --parameters \
    location=southeastasia \
    appName=multi-factor-biz \
    environment=dev
```

### 3. Deploy Backend Container to Container Apps
```bash
# Get Container App environment ID
ENV_ID=$(az containerapp env list \
  --resource-group multi-factor-biz-dev-rg \
  --query "[0].id" -o tsv)

# Deploy container
az containerapp create \
  --name multi-factor-backend-dev \
  --resource-group multi-factor-biz-dev-rg \
  --environment $ENV_ID \
  --image YOUR_DOCKER_USERNAME/multi-factor-backend:latest \
  --target-port 8090 \
  --ingress external \
  --min-replicas 0 \
  --max-replicas 1 \
  --cpu 0.25 \
  --memory 0.5Gi
```

### 4. Deploy Frontend to Static Web Apps

#### Option A: Via Azure Portal (Easiest)
1. Go to [Azure Portal](https://portal.azure.com)
2. Search "Static Web Apps"
3. Click "Create"
4. Connect your GitHub repo
5. Set:
   - Build location: `project`
   - Output location: `project/dist`
   - Build command: `npm run build`

#### Option B: Via CLI
```bash
# Requires GitHub token (PAT)
az staticwebapp create \
  --name multi-factor-frontend-dev \
  --resource-group multi-factor-biz-dev-rg \
  --location southeastasia \
  --source https://github.com/YOUR_USER/your-repo \
  --branch main \
  --app-location "project" \
  --output-location "project/dist" \
  --app-build-command "npm run build"
```

---

## 🔗 Step 4: Configure CORS and URL

### 1. Get Backend URL
```bash
BACKEND_URL=$(az containerapp show \
  --name multi-factor-backend-dev \
  --resource-group multi-factor-biz-dev-rg \
  --query properties.configuration.ingress.fqdn -o tsv)

echo "Backend URL: https://$BACKEND_URL"
```

### 2. Update Frontend Env Variables
In `project/.env`:
```
VITE_BACKEND_URL=https://$BACKEND_URL
```

Or update in `src/App.tsx`:
```typescript
const BACKEND_URL = 'https://YOUR_BACKEND_URL';
```

### 3. Rebuild and Redeploy Frontend
```bash
cd project
npm run build

# Manually upload dist/ folder to Static Web Apps
# Or push to GitHub and let CI/CD handle it
```

---

## ⚙️ Step 5: Set Up Cost Monitoring

### 1. Create Budget Alert
```bash
az costmanagement budget create \
  --name "Free-Tier-Alert" \
  --scope "/subscriptions/YOUR_SUBSCRIPTION_ID" \
  --amount 5 \
  --time-period "Monthly" \
  --notifications '[
    {
      "type": "Forecasted",
      "thresholdType": "Forecasted",
      "threshold": 100,
      "recipient": "YOUR_EMAIL@example.com"
    }
  ]'
```

### 2. Enable Azure Cost Management
- Portal → Cost Management + Billing
- Set up alerts for spending

---

## ✅ Verification Checklist

- [ ] Frontend deployed to Static Web Apps
- [ ] Backend container running in Container Apps
- [ ] Backend URL accessible (https://YOUR_BACKEND_URL/decision returns 400 or similar)
- [ ] CORS headers present in backend responses
- [ ] Frontend can call backend `/decision` endpoint
- [ ] Test flow: Submit form → No "failed to fetch" error
- [ ] Azure Portal shows $0.00 estimated costs

---

## 📋 Troubleshooting

### "Failed to Fetch" Error
1. Check backend is running: `curl https://YOUR_BACKEND_URL/decision`
2. Verify CORS headers in response
3. Check browser DevTools Network tab
4. Update frontend `BACKEND_URL` to match actual URL

### Container App Not Starting
```bash
# Check logs
az containerapp logs show \
  --name multi-factor-backend-dev \
  --resource-group multi-factor-biz-dev-rg \
  --container-name java-backend \
  --follow
```

### Unexpected Charges
1. Check Container App metrics (scale activity)
2. Review Storage account access patterns
3. Disable Static Web App staging if not needed
4. Use budget alerts (see Step 5)

---

## 💰 Cost Optimization Tips

1. **Scale to 0:** Container Apps set to min 0 replicas ✓
2. **Region:** South East Asia has competitive free tier pricing ✓
3. **Storage:** Only upload Products.csv (~100KB) ✓
4. **No staging:** Static Web Apps free tier has no staging slots ✓
5. **Monitor:** Budget alerts enabled ✓

---

## 🔄 Continuous Deployment (Optional)

### GitHub Actions Pipeline
Push this file to `.github/workflows/deploy.yml`:
```yaml
name: Deploy
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Build frontend
        run: cd project && npm run build
      - name: Deploy to Azure
        run: |
          # Your deployment commands here
```

---

## 🎯 Next Steps

1. ✅ Deploy backend to Container Apps
2. ✅ Deploy frontend to Static Web Apps
3. ✅ Test API connectivity
4. ✅ Set up monitoring alerts
5. ✅ Document URLs and access info

---

## 📞 Support

- Azure Static Web Apps: https://docs.microsoft.com/azure/static-web-apps/
- Container Apps: https://docs.microsoft.com/azure/container-apps/
- Pricing Calculator: https://azure.microsoft.com/pricing/calculator/

**Estimated time to deploy: 15-30 minutes**
