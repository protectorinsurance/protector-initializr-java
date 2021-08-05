import os
import re
import requests
import shutil
from pathlib import Path

project_name = input("What is the name of the project (my-awesome-application)?\n")
namespace = input("What namespace should the application use? (no.protector.my.application)\n").lower()


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
    print(get_modules())
    namespace_folders = namespace.split('.')
    namespace_path = "/".join(namespace_folders)
    print(namespace_folders)
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


def find_and_replace_in_all_files(to_replace_list, replacement):
    folders_to_ignore = [".git", "gradle", ".gradle", "build", ".idea"]
    files_to_ignore = ["init.py"]
    top = os.getcwd()
    for dname, dirs, files in os.walk(top):
        folders = dname.split('\\')
        skip = False
        for folder_to_ignore in folders_to_ignore:
            if folder_to_ignore in folders:
                skip = True
        if skip:
            continue
        for fname in files:
            if fname in files_to_ignore:
                continue
            fpath = os.path.join(dname, fname)
            with open(fpath, encoding="utf-8") as f:
                s = f.read()
            for to_replace in to_replace_list:
                s = re.sub(to_replace, replacement, s, flags=re.IGNORECASE)
            with open(fpath, "w", encoding="utf-8") as f:
                f.write(s)


update_banner()
create_namespace()
find_and_replace_in_all_files(["protector-initializr-java", "protector-initializr"], project_name.lower())
find_and_replace_in_all_files(["no.protector.initializr"], namespace)
