import re
import os

source_manifest_path = r"D:\SoftwareData\AndroidStudioProjects\Touristdestinations\Touristdestinations\app\src\main\AndroidManifest.xml"
dest_manifest_path = r"D:\SoftwareData\AndroidStudioProjects\MatApp22\app\src\main\AndroidManifest.xml"

with open(source_manifest_path, 'r', encoding='utf-8') as f:
    src_content = f.read()

# Extract inner <application>...</application>
app_inner = re.search(r'<application[^>]*>(.*?)</application>', src_content, re.DOTALL).group(1)

# Find all <activity> tags
activities = re.findall(r'<activity[^>]*>.*?</activity>|<activity[^>]*/>', app_inner, re.DOTALL)

modified_activities = []
for act in activities:
    # Retain the exact xml structure of the activity tags, but modify android:name
    act = re.sub(r'android:name="\.', r'android:name=".td_pkg.', act)
    
    # Update MainActivity -> DestinationMain
    act = act.replace('android:name=".td_pkg.MainActivity"', 'android:name=".td_pkg.DestinationMain"')
    
    # Add android:theme if it doesn't have one
    if 'android:theme' not in act:
        # We need to insert android:theme="td_Theme.TouristDestinations"
        if re.search(r'android:exported="\w+"', act):
            act = re.sub(r'(android:exported="\w+")', r'\1\n            android:theme="@style/td_Theme.TouristDestinations"', act)
        else:
            # Fallback
            act = re.sub(r'(\s*>)', r'\n            android:theme="@style/td_Theme.TouristDestinations"\1', act, count=1)
            act = re.sub(r'(\s*/>)', r'\n            android:theme="@style/td_Theme.TouristDestinations"\1', act, count=1)
    modified_activities.append(act)

injection_text = "\n\n        <!-- ============================================================== -->\n"
injection_text += "        <!--  TouristDestinations activities (td_pkg)                       -->\n"
injection_text += "        <!-- ============================================================== -->\n\n        "
injection_text += "\n        ".join(modified_activities)
injection_text += "\n    "

with open(dest_manifest_path, 'r', encoding='utf-8') as f:
    dst_content = f.read()

# Find the start of the existing td_pkg block in MatApp22
start_match = re.search(r'\n\s*<!-- [^>]*TouristDestinations activities \(td_pkg\).*', dst_content)
end_idx = dst_content.rfind('</application>')

if start_match:
    start_idx = start_match.start()
    new_content = dst_content[:start_idx] + injection_text + dst_content[end_idx:]
else:
    new_content = dst_content[:end_idx] + injection_text[:-4] + dst_content[end_idx:]

with open(dest_manifest_path, 'w', encoding='utf-8') as f:
    f.write(new_content)

print("Manifest Integration Successful.")
