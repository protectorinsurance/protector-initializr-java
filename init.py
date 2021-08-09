import argparse
import os
import re
import requests
import shutil
from pathlib import Path

parser = argparse.ArgumentParser()

parser.add_argument('--p', help='Project name (my-awesome-application)', default=None)
parser.add_argument('--n', help='Namespace (no.protector.my.awesome.application)', default=None)
parser.add_argument('--pf', help='Persistence framework (none, jpa, jdbc)', default=None)

args = parser.parse_args()

project_name = args.p
namespace = args.n
persistence_framework = args.pf

if not project_name:
    project_name = input("What is the name of the project (my-awesome-application)?\n")

if not namespace:
    namespace = input("What namespace should the application use? (no.protector.my.awesome.application)\n").lower()

if not persistence_framework:
    persistence_framework = input("What persistence framework do you want? (none, jpa, jdbc)")


def update_banner():
    banner_text = project_name.replace('-', '++++').title()
    new_banner = requests.get("https://artii.herokuapp.com/make?text=" + banner_text).text
    with open('web/src/main/resources/banner.txt', 'w') as file:
        file.write(new_banner)


def get_modules():
    with open('settings.gradle') as gradle_settings:
        return [module.replace('include ', '').replace('\'', '') for module in
                re.findall(r'include \'.*\'', gradle_settings.read())]


def create_folders(path):
    Path(path).mkdir(parents=True, exist_ok=True)


def move_all(source_dir, target_dir):
    file_names = os.listdir(source_dir)
    for file_name in file_names:
        shutil.move(os.path.join(source_dir, file_name), target_dir)


def delete_empty_dirs(path):
    for root, dirs, files in os.walk(path, topdown=False):
        if not files and not dirs:
            os.rmdir(root)


def create_namespace():
    modules = get_modules()
    namespace_folders = namespace.split('.')
    namespace_path = "/".join(namespace_folders)
    for module in modules:
        base_paths = [f"{module}/src/main/java", f"{module}/src/test/groovy"]
        for base_path in base_paths:
            if not os.path.isdir(base_path):
                continue
            destination = f"{base_path}/{namespace_path}"
            create_folders(destination)
            source = f"{base_path}/no/protector/initializr"
            move_all(source, destination)
            delete_empty_dirs(base_path)


def get_allowed_folders():
    allowed_folders = get_modules()
    allowed_folders.append(".github")
    return allowed_folders


def get_available_files():
    allowed_folders = get_allowed_folders()
    files_to_ignore = ["init.py"]
    top = os.getcwd()
    files = []
    for dname, dirs, files in os.walk(top):
        folders = dname.split('\\')
        skip = True
        for folder in allowed_folders:
            if folder in folders:
                skip = False
                break
        if skip:
            continue
        for fname in files:
            if fname in files_to_ignore:
                continue
            files.append(fname)
    return files


def find_and_replace_in_file(to_replace_list, replacement, fpaths):
    for fpath in fpaths:
        with open(fpath, encoding="utf-8") as f:
            s = f.read()
            for to_replace in to_replace_list:
                s = re.sub(to_replace, replacement, s, flags=re.IGNORECASE)
        with open(fpath, "w", encoding="utf-8") as f:
            f.write(s)


def find_and_remove_lines_containing(search_term, fpaths):
    for fpath in fpaths:
        with open(fpath, encoding="utf-8") as f:
            lines = f.readlines()
        with open(fpath, "w", encoding="utf-8") as f:
            for line in lines:
                if search(search_term):
                    continue
                f.write(line)


def delete_dir(dir):
    shutil.rmtree(f"./{dir}/")


def insert_dependency:
    print("How do I do this?")


def set_persistence_framework():
    persistence_folders = {
        "none": {
            "folder": "domain-no-database",
            "dependencies": []
        },
        "jdbc": {
            "folder": "domain",
            "dependencies": ["org.springframework.boot:spring-boot-starter-data-jdbc"]
        },
        "jpa": {
            "folder": "domain-jpa",
            "dependencies": ["org.springframework.boot:spring-boot-starter-data-jdbc"]
        },
    }

    dependencies_to_remove = []

    for k, v in persistence_folders:
        if k != persistence_framework:
            delete_dir(v.get("folder"))
            for dependency in v.get("dependencies"):
                dependencies_to_remove.append(dependency)

    os.rename(persistence_folders.get(persistence_framework).get("folder"), "domain")

    _files = get_available_files()
    for dependency in dependencies_to_remove:
        find_and_remove_lines_containing(dependency, _files)


def validate():
    if ' ' in project_name:
        raise Exception("Project name cannot contain spaces")
    if ' ' in namespace:
        raise Exception("Namespace cannot contain spaces")
    if persistence_framework not in ["none", "jdbc", "jpa"]:
        raise Exception("You can only pick between none, jdbc and jpa")


validate()

# print("Updating banner...")
# update_banner()

# print("Creating new namespace...")
# create_namespace()

files = get_available_files()
print("Replacing references to initializr...")
find_and_replace_in_file(["protector-initializr-java", "protector-initializr"], project_name.lower(), files)
find_and_replace_in_file(["no.protector.initializr"], namespace, files)

titled_project_name = project_name.replace('-', ' ').title().replace(' ', '')
find_and_replace_in_file(["getProtectorInitializrContainer"], f"get{titled_project_name}Container", files)
find_and_replace_in_file(["createProtectorInitializrContainer"], f"create{titled_project_name}Container", files)
find_and_replace_in_file(["createBaseProtectorInitializrContainer"], f"createBase{titled_project_name}Container", files)
titled_project_name_first_lowercase = titled_project_name[0].lower() + titled_project_name[1:]
find_and_replace_in_file(["protectorInitializrContainer"], f"{titled_project_name_first_lowercase}Container", files)
find_and_replace_in_file(["initializrBaseUrl"], f"{titled_project_name_first_lowercase}BaseUrl", files)

find_and_replace_in_file(["initializr"], project_name.lower(), ["Web.SystemTest.Dockerfile"])

set_persistence_framework()

print("Done! Remember to go through the edits and verify the changes :)")
