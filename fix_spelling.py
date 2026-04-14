import os
import re

files_to_update = [
    "app/src/main/res/layout/activity_hotel_detail.xml",
    "app/src/main/res/layout/tourist_activity_detail.xml",
    "app/src/main/java/com/myapplication/matapp2/checkout_/CheckoutActivity.java"
]

for file_path in files_to_update:
    if os.path.exists(file_path):
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Simple string replacements for UI text
        content = content.replace("TOTAL TRAVELERS", "TOTAL TRAVELLERS")
        content = content.replace("Decrease travelers", "Decrease travellers")
        content = content.replace("Increase travelers", "Increase travellers")
        content = content.replace(" traveler)", " traveller)")
        content = content.replace(" travelers)", " travellers)")
        content = content.replace(" Traveler\"", " Traveller\"")
        content = content.replace(" Travelers\"", " Travellers\"")

        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

print("Traveler spelling updated successfully.")
