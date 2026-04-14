import os
import re

# 1. Update Bottom Navigation Icons
bottom_nav_layouts = [
    "app/src/main/res/layout/mat_layout_bottom_nav.xml",
    "app/src/main/res/layout/mat_activity_varanasi.xml",
    "app/src/main/res/layout/mat_activity_profile.xml",
    "app/src/main/res/layout/mat_activity_jaipur.xml",
    "app/src/main/res/layout/mat_activity_goa.xml",
    "app/src/main/res/layout/mat_activity_chennai.xml",
    "app/src/main/res/layout/mat_activity_agra.xml"
]

for layout in bottom_nav_layouts:
    if os.path.exists(layout):
        with open(layout, 'r', encoding='utf-8') as f:
            c = f.read()

        # Update Bookings Icon to 💵 layout
        c = re.sub(
            r'<TextView\s+android:layout_width="30dp"\s+android:layout_height="30dp"\s+android:background="@drawable/checkout_bg_section_icon"\s+android:text=".*?"\s+android:textSize="14sp"\s+android:gravity="center"\s*android:includeFontPadding="false"/>',
            '<TextView android:layout_width="34dp" android:layout_height="34dp" android:background="@drawable/checkout_bg_section_icon" android:text="💵" android:textSize="16sp" android:gravity="center"/>',
            c, flags=re.DOTALL
        )

        # Update Reward Points Icon to ✨ layout
        c = re.sub(
            r'<TextView\s+android:layout_width="30dp"\s+android:layout_height="30dp"\s+android:background="@drawable/checkout_bg_section_icon"\s+android:text="✨"\s+android:textSize="14sp"\s+android:gravity="center"\s*android:includeFontPadding="false"/>',
            '<TextView android:layout_width="34dp" android:layout_height="34dp" android:background="@drawable/checkout_bg_section_icon" android:text="✨" android:textSize="16sp" android:gravity="center"/>',
            c, flags=re.DOTALL
        )

        with open(layout, 'w', encoding='utf-8') as f:
            f.write(c)


# 2. Add isTaskRoot() to SplashActivity.java
splash_file = "app/src/main/java/com/myapplication/matapp2/mat_project_pkg/SplashActivity.java"
if os.path.exists(splash_file):
    with open(splash_file, 'r', encoding='utf-8') as f:
        c = f.read()
    if 'isTaskRoot()' not in c:
        c = c.replace(
            'super.onCreate(savedInstanceState);\n',
            'super.onCreate(savedInstanceState);\n        if (!isTaskRoot()) {\n            finish();\n            return;\n        }\n'
        )
        with open(splash_file, 'w', encoding='utf-8') as f:
            f.write(c)


# 3. Add statusBarColor and navigationBarColor to themes.xml for mat_primary themes
themes_file = "app/src/main/res/values/themes.xml"
if os.path.exists(themes_file):
    with open(themes_file, 'r', encoding='utf-8') as f:
        c = f.read()
    
    # Check Theme.MatApp2
    if 'android:statusBarColor' not in c.split('<style name="Base.Theme.MatApp2"')[1].split('</style>')[0]:
        c = c.replace(
            '<style name="Base.Theme.MatApp2" parent="Theme.Material3.DayNight.NoActionBar">\n        <!-- Customize your light theme here. -->\n        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->\n    </style>',
            '<style name="Base.Theme.MatApp2" parent="Theme.Material3.DayNight.NoActionBar">\n        <item name="android:statusBarColor">@color/mat_secondary</item>\n        <item name="android:navigationBarColor">@color/mat_secondary</item>\n    </style>'
        )

    # Check mat_Base.Theme.MAT_PROJECT
    if 'android:statusBarColor' not in c.split('<style name="mat_Base.Theme.MAT_PROJECT"')[1].split('</style>')[0]:
         c = c.replace(
            '<style name="mat_Base.Theme.MAT_PROJECT" parent="Theme.Material3.DayNight.NoActionBar">\n        <!-- Customize your light theme here. -->\n        <!-- <item name="mat_colorPrimary">@color/mat_my_light_primary</item> -->\n    </style>',
            '<style name="mat_Base.Theme.MAT_PROJECT" parent="Theme.Material3.DayNight.NoActionBar">\n        <item name="android:statusBarColor">@color/mat_secondary</item>\n        <item name="android:navigationBarColor">@color/mat_secondary</item>\n    </style>'
         )

    with open(themes_file, 'w', encoding='utf-8') as f:
        f.write(c)


# 4. Remove 'My Bookings' from ProfileActivity
profile_layout = "app/src/main/res/layout/checkout_activity_profile.xml"
if os.path.exists(profile_layout):
    with open(profile_layout, 'r', encoding='utf-8') as f:
        c = f.read()
    # Remove MyBookings LinearLayout
    c = re.sub(
        r'<LinearLayout android:id="@+id/btnMyBookings".*?</LinearLayout>\s*<View android:layout_width="match_parent" android:layout_height="1dp" android:background="@color/checkout_divider"/>\s*',
        '',
        c, flags=re.DOTALL
    )
    with open(profile_layout, 'w', encoding='utf-8') as f:
        f.write(c)

profile_java = "app/src/main/java/com/myapplication/matapp2/checkout_/ProfileActivity.java"
if os.path.exists(profile_java):
    with open(profile_java, 'r', encoding='utf-8') as f:
        c = f.read()
    c = re.sub(
        r'findViewById\(R\.id\.btnMyBookings\)\.setOnClickListener\(.*?\}\);',
        '',
        c, flags=re.DOTALL
    )
    with open(profile_java, 'w', encoding='utf-8') as f:
        f.write(c)

# 4b. Remove back button from checkout_activity_my_bookings.xml
bookings_layout = "app/src/main/res/layout/checkout_activity_my_bookings.xml"
if os.path.exists(bookings_layout):
    with open(bookings_layout, 'r', encoding='utf-8') as f:
        c = f.read()
    c = re.sub(
        r'<ImageButton android:id="@+id/btnBack".*?android:contentDescription="Back" />',
        '',
        c, flags=re.DOTALL
    )
    # Also fix header gravity since the button is gone
    c = c.replace('android:gravity="center_vertical"', 'android:gravity="center"')
    with open(bookings_layout, 'w', encoding='utf-8') as f:
        f.write(c)

bookings_java = "app/src/main/java/com/myapplication/matapp2/checkout_/MyBookingsActivity.java"
if os.path.exists(bookings_java):
    with open(bookings_java, 'r', encoding='utf-8') as f:
        c = f.read()
    c = re.sub(
        r'findViewById\(R\.id\.btnBack\)\.setOnClickListener\(.*?\}\);',
        '',
        c, flags=re.DOTALL
    )
    with open(bookings_java, 'w', encoding='utf-8') as f:
        f.write(c)

print("Mass script updates completely executed.")
