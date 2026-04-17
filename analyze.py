import os
import re

def analyze_java_project(src_dir):
    stats = {
        "files": 0,
        "loc": 0,
        "sloc": 0,
        "comments": 0,
        "blank": 0,
        "classes": 0,
        "concrete_classes": 0,
        "abstract_classes": 0,
        "enums": 0,
        "records": 0,
        "interfaces": 0,
        "nested_classes": 0,
        "methods": 0,
        "public_methods": 0,
        "private_methods": 0,
        "protected_methods": 0,
        "default_methods": 0,
        "variables": 0,
        "instance_vars": 0,
        "static_vars": 0,
        "constants": 0,
    }

    import glob
    
    for root, _, files in os.walk(src_dir):
        for file in files:
            if file.endswith(".java"):
                stats["files"] += 1
                with open(os.path.join(root, file), "r", encoding="utf-8", errors="ignore") as f:
                    content = f.read()
                    lines = content.split('\n')
                    stats["loc"] += len(lines)
                    
                    in_block_comment = False
                    for line in lines:
                        stripped = line.strip()
                        if not stripped:
                            stats["blank"] += 1
                            continue
                            
                        if in_block_comment:
                            stats["comments"] += 1
                            if "*/" in stripped:
                                in_block_comment = False
                        else:
                            if stripped.startswith("/*"):
                                stats["comments"] += 1
                                if "*/" not in stripped:
                                    in_block_comment = True
                            elif stripped.startswith("//"):
                                stats["comments"] += 1
                            else:
                                stats["sloc"] += 1

                    # Count structures via regex
                    # Very naive, just for approximations as requested
                    stats["concrete_classes"] += len(re.findall(r'\bclass\s+\w+', content)) - len(re.findall(r'\babstract\s+class\s+\w+', content))
                    stats["abstract_classes"] += len(re.findall(r'\babstract\s+class\s+\w+', content))
                    stats["enums"] += len(re.findall(r'\benum\s+\w+', content))
                    stats["records"] += len(re.findall(r'\brecord\s+\w+', content))
                    stats["interfaces"] += len(re.findall(r'\binterface\s+\w+', content))
                    
                    methods = re.findall(r'\b(public|private|protected)?\s+(?:static\s+)?(?:final\s+)?[\w<>,\[\]]+\s+\w+\s*\([^)]*\)\s*(?:throws\s+[\w,\s]+)?\s*\{', content)
                    stats["methods"] += len(methods)
                    for m in methods:
                        if m == "public": stats["public_methods"] += 1
                        elif m == "private": stats["private_methods"] += 1
                        elif m == "protected": stats["protected_methods"] += 1
                        else: stats["default_methods"] += 1

    stats["classes"] = stats["concrete_classes"] + stats["abstract_classes"]
    
    for k, v in stats.items():
        print(f"{k}: {v}")

analyze_java_project("e:/RedLolli/src/main/java")
