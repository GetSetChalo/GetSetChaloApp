import os
import re

files_to_update = [
    "app/src/main/res/layout/mat_layout_bottom_nav.xml",
    "app/src/main/res/layout/mat_activity_varanasi.xml",
    "app/src/main/res/layout/mat_activity_profile.xml",
    "app/src/main/res/layout/mat_activity_jaipur.xml",
    "app/src/main/res/layout/mat_activity_home_copy.xml",
    "app/src/main/res/layout/mat_activity_goa.xml",
    "app/src/main/res/layout/mat_activity_chennai.xml",
    "app/src/main/res/layout/mat_activity_agra.xml"
]

for file_path in files_to_update:
    if os.path.exists(file_path):
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()

        # Fix broken Bookings Icon
        content = re.sub(
            r'<TextView\s+android:layout_width="30dp"\s+android:layout_height="30dp"\s+android:background="@drawable/checkout_bg_section_icon"\s+android:text=".*?"\s+android:textSize="14sp"\s+android:gravity="center"/>',
            '<TextView android:layout_width="30dp" android:layout_height="30dp" android:background="@drawable/checkout_bg_section_icon" android:text="🎫" android:textSize="14sp" android:gravity="center" android:includeFontPadding="false"/>',
            content, flags=re.DOTALL
        )

        # Fix the Explore icon to Reward points
        content = re.sub(
            r'<ImageView\s+android:layout_width="30dp"\s+android:layout_height="30dp"\s+android:src="@drawable/mat_explore_icon"/?>',
            '<TextView android:layout_width="30dp" android:layout_height="30dp" android:background="@drawable/checkout_bg_section_icon" android:text="✨" android:textSize="14sp" android:gravity="center" android:includeFontPadding="false"/>',
            content, flags=re.DOTALL
        )
        # Handle cases where attributes might be on same line
        content = re.sub(
            r'<ImageView android:layout_width="30dp" android:layout_height="30dp" android:src="@drawable/mat_explore_icon"\s*/>',
            '<TextView android:layout_width="30dp" android:layout_height="30dp" android:background="@drawable/checkout_bg_section_icon" android:text="✨" android:textSize="14sp" android:gravity="center" android:includeFontPadding="false"/>',
            content
        )

        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

print("Icons updated successfully.")
