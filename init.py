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


def skip_folder(dname, top_folder, allowed_folders):
    if dname == top_folder:
        return False
    folders = dname.split(os.sep)
    for allowed_folder in allowed_folders:
        if allowed_folder in folders:
            return False
    return True


def get_available_files():
    allowed_folders = get_allowed_folders()
    files_to_ignore = ["init.py"]
    top = os.getcwd()
    available_files = []
    for dname, dirs, _files in os.walk(top):
        if skip_folder(dname, top, allowed_folders):
            continue
        for fname in _files:
            if fname in files_to_ignore:
                continue
            available_files.append(os.path.join(dname, fname))
    return available_files


def find_and_replace_in_files(to_replace_list, replacement, fpaths):
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
                if search_term not in line:
                    f.write(line)


def delete_empty_files():
    _files = get_available_files()
    files_to_delete = []
    for _file in _files:
        with open(_file, encoding="utf-8") as f:
            if len(f.read()) == 0:
                files_to_delete.append(_file)
    [os.remove(f) for f in files_to_delete]

def delete_dir(dir):
    shutil.rmtree(f"./{dir}/")


def set_persistence_framework():
    persistence_folders = {
        "none": "domain-no-database",
        "jdbc": "domain",
        "jpa": "domain-jpa"
    }

    for k, v in persistence_folders.items():
        if k != persistence_framework:
            delete_dir(v)

    os.rename(persistence_folders.get(persistence_framework), "domain")

    for folder_name in persistence_folders.values():
        if folder_name == 'domain':
            continue
        find_and_remove_lines_containing(folder_name, ['./settings.gradle'])

    if persistence_framework == "none":
        clean_tag_content("DATABASE")
        find_and_replace_in_files([", PersistenceConfig"], '', get_available_files())


def is_not_import_line(line):
    return not re.match(r'^import .*\.[A-Za-z]+$', line)


def can_write(current_line, lines):
    if is_not_import_line(current_line):
        return True
    import_type = current_line.split('.')[-1].strip()
    return [line for line in lines if import_type in line and line != current_line]


def remove_unused_imports():
    _files = get_available_files()
    for fpath in _files:
        with open(fpath, encoding="utf-8") as f:
            lines = f.readlines()
        with open(fpath, "w", encoding="utf-8") as f:
            for line in lines:
                if can_write(line, lines):
                    f.write(line)


def generate_initializr_tags(tag):
    comment_prefixes = ["//", "-- ", "<!-- ", "# "]
    return [f"{prefix}INITIALIZR:{tag}" for prefix in comment_prefixes]


def clean_tag_content(tag):
    tags = generate_initializr_tags(tag)
    _files = get_available_files()
    for fpath in _files:
        with open(fpath, encoding="utf-8") as f:
            lines = f.readlines()
        with open(fpath, "w", encoding="utf-8") as f:
            write = True
            for line in lines:
                if len([i for i in tags if i in line]) > 0:
                    write = not write
                if write:
                    f.write(line)


def clean_initializr_tags():
    _files = get_available_files()
    find_and_remove_lines_containing('//INITIALIZR:', _files)


def validate():
    if ' ' in project_name:
        raise Exception("Project name cannot contain spaces")
    if ' ' in namespace:
        raise Exception("Namespace cannot contain spaces")
    if persistence_framework not in ["none", "jdbc", "jpa"]:
        raise Exception("You can only pick between none, jdbc and jpa")


validate()

print("Updating banner...")
update_banner()

print("Setting persistence framework...")
set_persistence_framework()

print("Creating new namespace...")
create_namespace()

files = get_available_files()
print("Replacing references to initializr...")
find_and_replace_in_files(["protector-initializr-java", "protector-initializr"], project_name.lower(), files)
find_and_replace_in_files(["no.protector.initializr"], namespace, files)

titled_project_name = project_name.replace('-', ' ').title().replace(' ', '')
find_and_replace_in_files(["getProtectorInitializrContainer"], f"get{titled_project_name}Container", files)
find_and_replace_in_files(["createProtectorInitializrContainer"], f"create{titled_project_name}Container", files)
find_and_replace_in_files(["createBaseProtectorInitializrContainer"], f"createBase{titled_project_name}Container",
                          files)

titled_project_name_first_lowercase = titled_project_name[0].lower() + titled_project_name[1:]
find_and_replace_in_files(["protectorInitializrContainer"], f"{titled_project_name_first_lowercase}Container", files)
find_and_replace_in_files(["initializrBaseUrl"], f"{titled_project_name_first_lowercase}BaseUrl", files)

find_and_replace_in_files(["initializr"], project_name.lower(), ["Web.SystemTest.Dockerfile"])

remove_unused_imports()
clean_initializr_tags()
delete_empty_files()

print("Done! Remember to go through the edits and verify the changes :)")
