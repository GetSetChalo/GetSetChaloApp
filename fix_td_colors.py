import os
import re

color_file = "app/src/main/res/values/colors.xml"
if os.path.exists(color_file):
    with open(color_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # td_colorBackground to #2D0B4E
    content = re.sub(r'<color name="td_colorBackground">.*?</color>', '<color name="td_colorBackground">#2D0B4E</color>', content)
    # td_colorSurface to a lighter version of #2D0B4E -> #3A1361
    content = re.sub(r'<color name="td_colorSurface">.*?</color>', '<color name="td_colorSurface">#3A1361</color>', content)
    # td_colorButtonBg also to a lighter version
    content = re.sub(r'<color name="td_colorButtonBg">.*?</color>', '<color name="td_colorButtonBg">#3A1361</color>', content)
    # td_colorCardBorder to even lighter #4A1A7D
    content = re.sub(r'<color name="td_colorCardBorder">.*?</color>', '<color name="td_colorCardBorder">#4A1A7D</color>', content)

    with open(color_file, 'w', encoding='utf-8') as f:
        f.write(content)

print("Tourist Destination Colors updated successfully.")
