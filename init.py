import os
import re
import requests

project_name = input("What is the name of the project (no spaces, example: my-project)?\n")


def update_banner():
    banner_text = project_name.replace('-', '++++').title()
    new_banner = requests.get("https://artii.herokuapp.com/make?text="+banner_text).text
    with open('web/src/main/resources/banner.txt', 'w') as file:
        file.write(new_banner)


def rename():
    folders_to_ignore = [".git", "gradle", ".gradle", "build", ".idea"]
    files_to_ignore = ["init.py"]
    to_replace_list = ["protector-initializr-java", "protector-initializr"]
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
                s = re.sub(to_replace, project_name.lower(), s, flags=re.IGNORECASE)
            with open(fpath, "w", encoding="utf-8") as f:
                f.write(s)


update_banner()
rename()
