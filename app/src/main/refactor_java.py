import os
import glob

tab_map = {
    "HomeActivity.java": "home",
    "ExploreActivity.java": "explore",
    "NotifActivity.java": "notifications",
    "ProfileActivity.java": "profile"
}

ignore_list = ["SplashActivity", "Login", "Register", "MatApp", "BaseNavActivity", "NavigationBarActivity", "DestinationData"]

def update_java(filepath):
    filename = os.path.basename(filepath)
    for ign in ignore_list:
        if ign in filename:
            return
            
    try:
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
    except:
        return

    # Check if it has a class definition
    if "class " + filename.replace(".java","") not in content:
        return

    # Skip interfaces/adapters etc
    if "extends RecyclerView.Adapter" in content or "extends Fragment" in content:
        return

    if "extends AppCompatActivity" in content or "extends Activity" in content:
        if "extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity" in content: return

        tab = tab_map.get(filename, "explore")
        content = content.replace("extends AppCompatActivity", "extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity")
        
        # careful with generic "extends Activity" to not break strings, but it usually matches "extends Activity {" or something.
        content = content.replace("extends Activity ", "extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity ")
        content = content.replace("extends Activity{", "extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {")
        content = content.replace("extends Activity\n", "extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity\n")
        
        # Remove manual initializations
        content = content.replace('NavigationBarActivity.setup(this,', '// removed')

        # Insert getActiveTab at the end
        idx = content.rfind("}")
        if idx != -1:
            insertion = f"\n    @Override\n    protected String getActiveTab() {{\n        return \"{tab}\";\n    }}\n}}"
            content = content[:idx] + insertion

        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
            print(f"Updated {filename}")

java_files = glob.glob("**/*.java", recursive=True)
count = 0
for jf in java_files:
    update_java(jf)
    count += 1
print(f"Scanned {count} files.")
