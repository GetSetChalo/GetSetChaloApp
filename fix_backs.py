import os
import re

files_to_update = [
    "app/src/main/java/com/myapplication/matapp2/checkout_/MyBookingsActivity.java",
    "app/src/main/java/com/myapplication/matapp2/checkout_/ProfileActivity.java"
]

for file_path in files_to_update:
    if os.path.exists(file_path):
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # For ProfileActivity:
        if "btnProfileBack" in content:
            # We want to replace the whole findViewById(R.id.btnProfileBack).setOnClickListener block
            content = re.sub(
                r'findViewById\(R\.id\.btnProfileBack\)\.setOnClickListener\(v -> \{.*?\}\);',
                'findViewById(R.id.btnProfileBack).setOnClickListener(v -> finish());',
                content, flags=re.DOTALL
            )
            
        # For MyBookingsActivity:
        if "btnBack" in content:
            content = re.sub(
                r'findViewById\(R\.id\.btnBack\)\.setOnClickListener\(v -> \{.*?\}\);',
                'findViewById(R.id.btnBack).setOnClickListener(v -> finish());',
                content, flags=re.DOTALL
            )

        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

print("Back buttons updated successfully.")
