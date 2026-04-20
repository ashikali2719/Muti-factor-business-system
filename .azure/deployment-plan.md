# Azure Deployment Plan - Multi-Factor Business System

**Status:** In Progress - Generating Artifacts  
**Created:** 2026-04-20  
**Last Updated:** 2026-04-20  
**Subscription:** Default  
**Region:** Southeast Asia (southeastasia)  
**Cost Target:** $0/month (free tier only)

---

## Phase 1: Planning

### 1. Workspace Analysis
- **Mode:** MODIFY (existing full-stack app)
- **Structure:** 
  - React/TypeScript frontend in `project/` (Vite)
  - Java backend in `src/main/java/` (Maven, Jetty servlets)
  - Shared data in `data/Products.csv`

### 2. Requirements
- Scale: Small/Prototype (free tier)
- Budget: $0/month (free tier only)
- Users: Development/testing
- Region: South East Asia (southeastasia)
- Goal: Zero-cost full-stack deployment

### 3. Codebase Scan
**Frontend:**
- React 18, TypeScript, Vite
- Tailwind CSS, Recharts charts
- Dependencies: lucide-react, supabase-js

**Backend:**
- Java 17, Jakarta Servlet 5.0
- Maven build, Jetty runtime
- Dependencies: Selenium (web scraping), Apache Commons Math (ML)
- CSV data loading from `data/Products.csv`

### 4. Recipe Selection (TBD)
- Recommended: **Azure Developer CLI (AZD)** with Bicep
- Alternative: Manual Bicep or Terraform

### 5. Architecture Plan (Free Tier Optimized)
**Proposed Stack:**
- **Frontend:** Azure Static Web Apps (free tier - 1 app per subscription)
  - Hosts React build artifacts
  - Global CDN included
  - Custom domains supported
  
- **Backree Tier Constraints & Optimization
**Azure Static Web Apps (Free):**
- 1 free app per subscription ✓
- Unlimited bandwidth ✓
- Auto-HTTPS ✓
- No staging slots

**Azure Container Apps (Consumption Plan):**
- Pay per execution (~$0.000278/GB/second)
- Scale to 0 replicas when idle ✓
- No always-on charges ✓
- Billing only during execution

**Storage (Free):**
- 100 GB/month standard tier
- First month: $5.50 credit
- After that: Keep usage under free limits

### 7. Finalized Plan
- [x] Architecture confirmed with user
- [x] Azure context: Default subscription, South East Asia region
- [ ] Generate Dockerfiles and infrastructure code
- [ ] Provide deployment script
  - Runs Java WAR in Docker container
  
- **Storage:** Azure Storage (free tier - 100 items soft limit)
  - Blob Storage for Products.csv
  - First 50 GB/month included in free tier

- **Communication:** CORS-enabled REST API
  - `/decision` endpoint on backend
  - Frontend calls via HTTPS

### 6. Finalized Plan
- [ ] Confirm architecture choice with user
- [ ] Detect Azure subscription and location
- [ ] Generate Dockerfiles and infrastructure code
- [ ] Deploy and verify

---

## Phase 2: Execution (READY FOR DEPLOYMENT)

### Artifacts Generated ✓
- [x] `Dockerfile` - Multi-stage Java build (128MB RAM, 0.25 vCPU)
- [x] `infra/main.bicep` - Bicep template for all resources
- [x] `infra/main.bicepparam` - Parameters file
- [x] `infra/DEPLOYMENT_GUIDE.md` - Step-by-step deployment
- [x] `infra/deploy.sh` - Automated deployment script
- [x] `project/.env.example` - Environment config template
- [x] Frontend updated for environment variables

### Architecture Decisions ✓
- **Frontend:** Azure Static Web Apps (Free tier, 1 app/subscription)
- **Backend:** Azure Container Apps Consumption (Pay-per-use, scales to 0)
- **Storage:** Azure Blob Storage (First 50GB free)
- **Region:** South East Asia (southeastasia)
- **Cost Target:** $0/month ✓

### Free Tier Optimizations Applied ✓
1. Container App: min replicas = 0 (scales to zero) → $0 when idle
2. Memory: 0.5GB (minimum for Java) → Lower compute costs
3. CPU: 0.25 vCPU (minimum) → Lower compute costs
4. Storage: LRS (locally redundant) → Lowest storage cost
5. Static Web Apps: Free tier → No charges
6. No staging slots → No extra costs

### CORS & Networking ✓
- Static Web Apps → Container Apps HTTPS
- CORS headers configured for frontend/backend communication
- Environment variables for dynamic URL configuration

### Cost Monitoring ✓
- Budget alerts included in guide
- Azure Cost Management setup instructions
- Monthly cost tracking

---

## Next Step: Deploy

Follow `infra/DEPLOYMENT_GUIDE.md` or run:
```bash
bash infra/deploy.sh
```

Estimated deployment time: 15-30 minutes

