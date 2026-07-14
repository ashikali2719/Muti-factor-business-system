import sys
import os
import joblib
import pandas as pd

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_PATH = os.path.join(BASE_DIR, "random_forest_model.pkl")

model = joblib.load(MODEL_PATH)

try:
    inventory = float(sys.argv[1])
    units_sold = float(sys.argv[2])
    units_ordered = float(sys.argv[3])
    price = float(sys.argv[4])
    discount = float(sys.argv[5])
    competitor_price = float(sys.argv[6])

    input_data = {
        "Store ID": [1],
        "Product ID": [1],
        "Category": [1],
        "Region": [1],
        "Inventory Level": [inventory],
        "Units Sold": [units_sold],
        "Units Ordered": [units_ordered],
        "Price": [price],
        "Discount": [discount],
        "Weather Condition": [1],
        "Promotion": [1],
        "Competitor Pricing": [competitor_price],
        "Seasonality": [1],
        "Epidemic": [0]
    }

    input_df = pd.DataFrame(input_data)
    prediction = model.predict(input_df)

    print(round(prediction[0], 2))

except Exception as e:
    print("ERROR:", str(e))