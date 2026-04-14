import os

color_file = "app/src/main/res/values/colors.xml"
if os.path.exists(color_file):
    with open(color_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace hd_bg
    content = content.replace('<color name="hd_bg">#16082C</color>', '<color name="hd_bg">#2D0B4E</color>')
    # Replace tourist_bg_dark
    content = content.replace('<color name="tourist_bg_dark">#0D0A2E</color>', '<color name="tourist_bg_dark">#2D0B4E</color>')
    
    with open(color_file, 'w', encoding='utf-8') as f:
        f.write(content)

layouts = [
    "app/src/main/res/layout/tourist_activity_packages.xml",
    "app/src/main/res/layout/tourist_activity_detail.xml"
]

for l in layouts:
    if os.path.exists(l):
        with open(l, 'r', encoding='utf-8') as f:
            c = f.read()
        
        c = c.replace('#0D0A2E', '#2D0B4E')
        c = c.replace('#150E40', '#2D0B4E')
        
        with open(l, 'w', encoding='utf-8') as f:
            f.write(c)

print("Colors updated to #2D0B4E.")
