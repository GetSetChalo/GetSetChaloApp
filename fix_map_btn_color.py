import os
import re
import glob

files = glob.glob('app/src/main/res/layout/td_activity_*.xml')

for filepath in files:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Regex to find btnOpenInMap Button block and replace its textColor
    # We match <Button ... id="@+id/btnOpenInMap" ... />
    
    def replacer(match):
        button_block = match.group(0)
        # replacing text color specifically
        button_block = re.sub(r'android:textColor="[^"]+"', 'android:textColor="#2D0B4E"', button_block)
        return button_block

    new_content = re.sub(r'<Button[^>]+android:id="@+id/btnOpenInMap"[^>]+>', replacer, content)

    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)

print("Updated text color of OPEN IN MAP buttons in " + str(len(files)) + " files.")
