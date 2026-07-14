import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import r2_score, mean_absolute_error, mean_squared_error
import numpy as np
import joblib

# Load dataset
df = pd.read_csv("demand_forecasting.csv")

print("Dataset Loaded Successfully")
print(df.head())

# Remove unnecessary columns
if "Date" in df.columns:
    df = df.drop(columns=["Date"])

# Convert categorical columns into numbers
categorical_columns = [
    "Store ID",
    "Product ID",
    "Category",
    "Region",
    "Weather Condition",
    "Seasonality",
    "Epidemic"
]

for col in categorical_columns:
    if col in df.columns:
        df[col] = df[col].astype("category").cat.codes

# Convert Promotion column
if "Promotion" in df.columns:
    df["Promotion"] = df["Promotion"].astype(str).map({
        "Yes": 1,
        "No": 0,
        "True": 1,
        "False": 0
    }).fillna(0)

# Features and target
X = df.drop(columns=["Demand"])
y = df["Demand"]

# Split dataset
X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.2,
    random_state=42
)

# Random Forest Model
model = RandomForestRegressor(
    n_estimators=100,
    random_state=42,
    n_jobs=-1
)

# Train model
model.fit(X_train, y_train)

# Predict
y_pred = model.predict(X_test)

# Evaluation
r2 = r2_score(y_test, y_pred)
mae = mean_absolute_error(y_test, y_pred)
rmse = np.sqrt(mean_squared_error(y_test, y_pred))

print("\n===== MODEL EVALUATION =====")
print("R2 Score:", round(r2, 4))
print("Accuracy Percentage:", round(r2 * 100, 2), "%")
print("MAE:", round(mae, 2))
print("RMSE:", round(rmse, 2))

# Save model
joblib.dump(model, "random_forest_model.pkl")

print("\nModel saved successfully!")