import os
import glob

# Mapping for replacements
replacements = {
    "com.TaskManagementTool_b72.Entity": "com.taskmanagementtool_b72.entity",
    "com.TaskManagementTool_b72.Enum": "com.taskmanagementtool_b72.enums",
    "com.TaskManagementTool_b72.Repository": "com.taskmanagementtool_b72.repository",
    "com.TaskManagementTool_b72.Security": "com.taskmanagementtool_b72.security",
    "com.TaskManagementTool_b72.DTO": "com.taskmanagementtool_b72.dto",
    "TaskManagementTool_b72.Service": "com.taskmanagementtool_b72.service",
    "com.TaskManagementTool_b72.Controller": "com.taskmanagementtool_b72.controller"
}

# 1. Move folders
os.system("mkdir -p src/main/java/com/taskmanagementtool_b72/service")
os.system("mv src/main/java/TaskManagementTool_b72/Service/* src/main/java/com/taskmanagementtool_b72/service/ 2>/dev/null")
os.system("mv src/main/java/com/taskmanagementtool_b72/Enum src/main/java/com/taskmanagementtool_b72/enums 2>/dev/null")

# Find all java files
files = glob.glob("src/main/java/**/*.java", recursive=True)

for file in files:
    with open(file, 'r') as f:
        content = f.read()
    
    modified = content
    for old, new in replacements.items():
        modified = modified.replace(old, new)
        
    if modified != content:
        with open(file, 'w') as f:
            f.write(modified)
        print(f"Updated {file}")

# Clean up empty old directory
os.system("rm -rf src/main/java/TaskManagementTool_b72")
