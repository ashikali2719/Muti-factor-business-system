# Multi-Factor Business System

> **A Smart Business Intelligence Platform with AI-Powered Demand Prediction**

A full-stack web application that empowers businesses to make data-driven decisions by analyzing multiple market factors, competitor pricing, and historical trends. The system combines a modern React frontend with a Java backend powered by machine learning to deliver actionable business insights.

---

## 🎯 Features

### Core Capabilities
- **Smart Demand Prediction** – AI-driven forecasting using historical data and market trends
- **Multi-Factor Analysis** – Evaluates stock levels, sales velocity, competitor pricing, and market demand
- **Real-Time Dashboards** – Interactive charts and visual analytics for quick insights
- **Price Intelligence** – Scrapes and aggregates prices from major e-commerce platforms (Amazon, Flipkart, Meesho)
- **Business Decision Engine** – Automatic recommendations with confidence scoring and risk assessment
- **Historical Data Tracking** – Maintains audit trails and historical decision records
- **Explainable AI** – Clear explanations for every recommendation, building trust in the system

### User Experience
- Clean, modern interface built with React and TypeScript
- Responsive design with Tailwind CSS
- Interactive charts using Recharts
- Real-time confidence and risk indicators
- Mobile-friendly dashboard

---

## 🧠 Machine Learning Feature

### Demand Prediction Engine

The system uses **linear regression and statistical analysis** to predict future demand based on:

- **Historical Sales Patterns** – Analyzes past sales data to identify trends
- **Seasonal Factors** – Recognizes seasonal demand fluctuations
- **Market Conditions** – Incorporates competitor pricing and market saturation
- **Input Factors** – Considers current stock levels, recent sales velocity, and competitor actions

**How It Works (Simple Explanation):**
The ML model learns from your historical product data to identify patterns between market factors and actual demand. When you input current market conditions, the model predicts what the demand will likely be. This prediction helps the system recommend whether to buy more stock, reduce prices, or hold—all with a confidence score indicating prediction reliability.

**Technical Approach:**
- **Algorithm**: Linear regression combined with rule-based decision logic
- **Data Source**: CSV-based historical product data
- **Accuracy Indicator**: Confidence score (0-100) shows how reliable each prediction is
- **Explainability**: Every prediction includes a human-readable explanation of the reasoning

This approach balances accuracy with interpretability—perfect for business users who need to understand *why* the system makes recommendations.

---

## 🛠️ Tech Stack

### Frontend
- **React 18** – Modern UI framework
- **TypeScript** – Type-safe JavaScript
- **Vite** – Lightning-fast build tool
- **Tailwind CSS** – Utility-first styling
- **Recharts** – Interactive charting library
- **PostCSS & Autoprefixer** – CSS processing

### Backend
- **Java 17** – Core runtime
- **Jakarta Servlet 5.0** – Web framework (modern Java)
- **Jetty** – Lightweight application server
- **Apache Commons Math** – Statistical computations for ML
- **Selenium + WebDriverManager** – Web scraping capabilities
- **Maven** – Build and dependency management

### Data & ML
- **CSV Data Loading** – Simple, scalable data ingestion
- **Linear Regression** – Demand prediction model
- **Decision Logic Engine** – Rule-based business recommendations

---

## 📁 Project Structure

```
Multi-Factor-Business-System/
├── project/                          # Frontend (React/TypeScript/Vite)
│   ├── src/
│   │   ├── components/              # React components
│   │   │   ├── Charts.tsx           # Data visualization
│   │   │   ├── HistoryDashboard.tsx # Historical records view
│   │   │   ├── InputCard.tsx        # Market factor inputs
│   │   │   ├── StatsGrid.tsx        # Key metrics display
│   │   │   ├── ConfidenceBar.tsx    # Confidence visualization
│   │   │   ├── BusinessInsight.tsx  # Insight panel
│   │   │   └── ...
│   │   ├── App.tsx                  # Main app component
│   │   ├── main.tsx                 # Entry point
│   │   └── index.css                # Global styles
│   ├── package.json                 # Frontend dependencies
│   ├── vite.config.ts              # Vite configuration
│   └── tsconfig.json               # TypeScript config
│
├── src/main/java/                   # Backend (Java)
│   ├── DemandService.java           # ML demand prediction service
│   ├── DecisionEngine.java          # Business decision logic
│   ├── DecisionServlet.java         # API endpoint handler
│   ├── MarketPriceService.java      # Price scraping & aggregation
│   ├── Product.java                 # Data models
│   ├── MarketData.java
│   └── ...
│
├── data/
│   └── Products.csv                 # Historical training data
│
├── pom.xml                          # Backend build configuration
├── README.md                        # This file
└── INTEGRATION_CHECKLIST.md         # Integration guide
```

---

## ⚡ Installation & How to Run

### Prerequisites
Before you start, make sure you have:
- **Node.js 16+** and **npm** (for frontend)
- **Java 17+** (for backend) – [Download JDK 17](https://adoptium.net/)
- **Maven 3.8+** (for backend builds) – [Download Maven](https://maven.apache.org/download.cgi)
- **Git** (to clone the repository)

### Step 1: Clone the Repository

```bash
git clone https://github.com/ashikali2719/Muti-factor-business-system.git
cd Muti-factor-business-system
```

### Step 2: Set Up and Run the Backend (Java)

Navigate to the backend directory and build with Maven:

```bash
# Set Java version (if needed)
set JAVA_HOME=C:\path\to\jdk-17  # Windows
export JAVA_HOME=/path/to/jdk-17  # macOS/Linux

# Build the backend
mvn clean install

# Run the backend server (starts on http://localhost:8090)
mvn jetty:run -Djetty.port=8090
```

The backend will start on **http://localhost:8090**

### Step 3: Set Up and Run the Frontend (React)

In a **new terminal**, navigate to the frontend directory:

```bash
cd project

# Install dependencies
npm install

# Start the development server
npm run dev
```

The frontend will start on **http://localhost:5173**

### Step 4: Open Your Browser

Visit **http://localhost:5173** to access the Multi-Factor Business System dashboard.

> **Note**: Make sure both the backend (port 8090) and frontend (port 5173) are running. The frontend will automatically connect to the backend API.

### Troubleshooting

| Issue | Solution |
|-------|----------|
| Backend won't start | Ensure Java 17+ is installed and `JAVA_HOME` is set correctly |
| Frontend won't load | Check that you're in the `project/` directory and ran `npm install` |
| "Cannot fetch from backend" | Verify backend is running on port 8090 and no firewall is blocking it |
| Port already in use | Change ports: `mvn jetty:run -Djetty.port=8091` or `npm run dev -- --port 3000` |

---

## 💡 Usage Instructions

### Analyzing Market Factors

1. **Open the Dashboard** – Navigate to http://localhost:5173
2. **Enter Market Data** – Fill in the Input Card with:
   - Product Name
   - Current Stock Level
   - Recent Sales Count
   - Your Current Price
   - Competitor's Price
3. **Click "Analyze"** – The system will:
   - Predict future demand using the ML model
   - Evaluate market conditions
   - Generate a business recommendation
4. **Review Results** – See:
   - Predicted demand level
   - Recommendation (Buy Stock / Don't Buy / Adjust Price)
   - Confidence score
   - Detailed explanation of reasoning
   - Historical trends and charts

### Understanding the Recommendation

The system provides three types of recommendations:

| Recommendation | When | Action |
|---|---|---|
| **BUY STOCK NOW** | High demand predicted & sufficient budget | Increase inventory |
| **DON'T BUY STOCK** | Low demand or risky conditions | Hold current inventory |
| **ADJUST PRICING** | Competitor undercutting or margin issues | Optimize price strategy |

Each recommendation includes a **confidence score** (0-100%) indicating prediction reliability.

---

## 🚀 Future Improvements

- [ ] **Advanced Time Series Forecasting** – Implement ARIMA/Prophet for seasonal patterns
- [ ] **Real-Time Price Updates** – Live price tracking from e-commerce APIs
- [ ] **Inventory Optimization** – Automated stock level recommendations
- [ ] **Multi-Region Support** – Analyze markets across different geographical regions
- [ ] **Custom ML Models** – Allow users to train models on their own data
- [ ] **Alert System** – Notify users when market conditions change significantly
- [ ] **Performance Metrics** – Dashboard showing model accuracy and ROI impact
- [ ] **Mobile App** – Native iOS/Android applications
- [ ] **API Documentation** – Swagger/OpenAPI specs for third-party integrations
- [ ] **Database Integration** – Replace CSV with SQL database for scalability

---

## 📊 Screenshots

> [Screenshots will be added here after deployment]

- Dashboard Overview
- Demand Prediction Results
- Historical Trends Chart
- Recommendation Details
- Input Analysis Form

---

## 🤝 Contributing

We welcome contributions! Please feel free to submit issues, fork the repository, and create pull requests.

---

## 📄 License

This project is open source and available under the MIT License.

---

## 📧 Contact & Support

For questions or support, please reach out via GitHub Issues or contact the development team.

---

## 🎓 Learning Resources

- [React Documentation](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Java Documentation](https://docs.oracle.com/en/java/javase/17/)
- [Machine Learning Basics](https://www.coursera.org/learn/machine-learning)
- [Recharts Gallery](https://recharts.org/)

---

**Built with ❤️ for smarter business decisions.**
