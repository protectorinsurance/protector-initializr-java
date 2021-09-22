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
parser.add_argument('--clean', help='Removes all initializr demo implementations', default=None)
parser.add_argument('--kafka_producer', help='Do you want Kafka Producer?', default=None)

args = parser.parse_args()

project_name = args.p
namespace = args.n
persistence_framework = args.pf
protected_paths = []

if not project_name:
    project_name = input("What is the name of the project (my-awesome-application)?\n")

if not namespace:
    namespace = input("What namespace should the application use? (no.protector.my.awesome.application)\n").lower()

if not persistence_framework:
    persistence_framework = input("What persistence framework do you want? (none, jpa, jdbc)\n")

if not args.kafka_producer:
    args.kafka_producer = input("Do you want Kafka producers? (y/n)\n")

if not args.clean:
    args.clean = input("Do you want to remove demo/initializr files?(y/n - y recommended)\n")

tags_to_clean = []


def parse_boolean_response(response):
    if isinstance(response, bool):
        return response
    if response.lower() in ('yes', 'true', 't', 'y', '1'):
        return True
    if response.lower() in ('no', 'false', 'f', 'n', '0'):
        return False
    raise argparse.ArgumentTypeError('Boolean value expected.')


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
    has_deleted_directories = True
    while has_deleted_directories:
        has_deleted_directories = False
        for root, dirs, files in os.walk(path, topdown=False):
            can_delete = True
            for protected_path in protected_paths:
                if not root.endswith(protected_path):
                    continue
                can_delete = False
                break
            if can_delete and not files and not dirs:
                os.rmdir(root)
                has_deleted_directories = True


def create_namespace():
    modules = get_modules()
    namespace_folders = namespace.split('.')
    protected_paths.append(os.sep.join(namespace_folders))
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


def get_allowed_folders():
    allowed_folders = get_modules()
    allowed_folders.append(".github")
    allowed_folders.append("flyway")
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
        s = read(fpath)
        if not s:
            continue
        for to_replace in to_replace_list:
            s = re.sub(to_replace, replacement, s, flags=re.IGNORECASE)
        with open(fpath, "w", encoding="utf-8") as f:
            f.write(s)


def find_and_remove_lines_containing(search_term, fpaths):
    for fpath in fpaths:
        lines = read_lines(fpath)
        if not lines:
            continue
        with open(fpath, "w", encoding="utf-8") as f:
            for line in lines:
                if search_term not in line:
                    f.write(line)


def delete_empty_files():
    _files = get_available_files()
    files_to_delete = []
    for _file in _files:
        content = read(_file)
        if re.search('[a-zA-Z]', content):
            continue
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
        tags_to_clean.append("DATABASE")
        find_and_replace_in_files([", PersistenceConfig"], '', get_available_files())
        shutil.rmtree("flyway")
    else:
        protected_paths.append(f"flyway{os.sep}migrations")


def is_not_import_line(line):
    return not re.match(r'^import .*\.[A-Za-z]+$', line)


def can_write(current_line, lines):
    if is_not_import_line(current_line):
        return True
    import_type = current_line.split('.')[-1].strip()
    return [line for line in lines if import_type in line and line != current_line]


def read_lines(fpath):
    try:
        with open(fpath, encoding="utf-8") as f:
            return f.readlines()
    except:
        return None


def read(fpath):
    try:
        with open(fpath, encoding="utf-8") as f:
            return f.read()
    except:
        return None


def remove_unused_imports():
    _files = get_available_files()
    for fpath in _files:
        lines = read_lines(fpath)
        if not lines:
            continue
        with open(fpath, "w", encoding="utf-8") as f:
            for line in lines:
                if can_write(line, lines):
                    f.write(line)


def get_comment_prefixes():
    return ["//", "-- ", "<!-- ", "# ", "[comment]: # ("]


def get_initializer_prefix():
    return [f"{prefix}INITIALIZR:" for prefix in get_comment_prefixes()]


def is_one_of_tags_in_initializr_comment(tags, line):
    initializer_prefixes = get_initializer_prefix()
    if not line_is_initializr_comment(initializer_prefixes, line):
        return False
    for prefix in initializer_prefixes:
        line = line.replace(prefix, "").replace("-->", "").replace(")", "")
    for tag in tags:
        if tag in line:
            return True
    return False


def line_is_initializr_comment(initializer_prefixes, line):
    return len([i for i in initializer_prefixes if i in line]) > 0


def should_write_xml(lines_to_write):
    lines_that_are_not_initializr_comments = [
        line for line in lines_to_write if not line_is_initializr_comment(get_initializer_prefix(), line)]
    return len(lines_that_are_not_initializr_comments) > 1


def print_if_my_file(fpath, message):
    if "EmployeeController" in fpath:
        print(message)


def clean_tag_content(tags):
    _files = get_available_files()
    for fpath in _files:
        print_if_my_file(fpath, "Processing EmployeeController...")
        lines = read_lines(fpath)
        if not lines:
            continue
        print_if_my_file(fpath, "Found lines...")
        with open(fpath, "w", encoding="utf-8") as f:
            is_xml = fpath.endswith(".xml")
            write = True
            lines_to_write = []
            last_initializr_comment_line = None
            for line in lines:
                if is_one_of_tags_in_initializr_comment(tags, line):
                    print_if_my_file(fpath, "Found tag...")
                    if not last_initializr_comment_line:
                        last_initializr_comment_line = line
                    is_same = last_initializr_comment_line.strip() == line.strip()
                    if not is_same:
                        continue
                    last_initializr_comment_line = line
                    write = not write
                    print_if_my_file(fpath, "Skip writing...")
                    continue
                if write:
                    print_if_my_file(fpath, "Writing")
                    lines_to_write.append(line)
            if is_xml and not should_write_xml(lines_to_write):
                f.truncate(0)
                return
            string_form = '\n'.join(lines_to_write)
            print_if_my_file(fpath, f"Lines to write: {string_form}")
            [f.write(line) for line in lines_to_write]


def clean_initializr_tags():
    _files = get_available_files()
    [find_and_remove_lines_containing(tag, _files) for tag in get_initializer_prefix()]


def clean_all_double_empty_lines():
    _files = get_available_files()
    for fpath in _files:
        content = read(fpath)
        if not content:
            continue
        content = re.sub(r'\n\s*\n', '\n\n', content)
        with open(fpath, "w", encoding="utf-8") as f:
            f.write(content)


def is_comment_line(line):
    comment_symbols = ['*', "#", '//', '/*']
    return any(comment_symbol in line for comment_symbol in comment_symbols)


def get_files_that_contain_word(word, _files):
    files_that_contain_word = []
    files_to_ignore = ["README.md"]
    for fpath in _files:
        if any(file_to_ignore in fpath for file_to_ignore in files_to_ignore):
            continue
        content = read(fpath)
        if not content:
            continue
        if word not in content:
            continue
        lines = content.splitlines()
        for line in lines:
            if word not in line or is_comment_line(line):
                continue
            files_that_contain_word.append(fpath)
    return files_that_contain_word


def run_sanity_checks():
    _files = get_available_files()
    files_with_word = get_files_that_contain_word('initializr', _files)
    name_of_files_with_words = '\n'.join(files_with_word)
    if files_with_word:
        raise Exception(f"Files contain the word 'Initializr': \n{name_of_files_with_words}")
    if not kafka_producer:
        files_with_word = get_files_that_contain_word('kafka', _files)
        if files_with_word:
            raise Exception(f"Files contain the word 'kafka': \n{name_of_files_with_words}")


def validate():
    if ' ' in project_name:
        raise Exception("Project name cannot contain spaces")
    if ' ' in namespace:
        raise Exception("Namespace cannot contain spaces")
    if persistence_framework not in ["none", "jdbc", "jpa"]:
        raise Exception("You can only pick between none, jdbc and jpa")
    if not kafka_producer and not clean_initializr:
        raise Exception("Having demo code without kafka is currently too finicky. Not supported")


clean_initializr = parse_boolean_response(args.clean)
kafka_producer = parse_boolean_response(args.kafka_producer)

validate()

if clean_initializr:
    tags_to_clean.append("INITIALIZR-DEMO")

if not kafka_producer:
    tags_to_clean.append("KAFKA-PRODUCER")

print("Updating banner...")
update_banner()

print("Setting persistence framework...")
set_persistence_framework()

print("Creating new namespace...")
create_namespace()

clean_tag_content(tags_to_clean)

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

print("Doing some house cleaning...")
remove_unused_imports()
clean_initializr_tags()
delete_empty_files()
delete_empty_dirs('./')
clean_all_double_empty_lines()

run_sanity_checks()

print("Done! Remember to go through the edits and verify the changes :)")
