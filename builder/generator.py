import os
import json
from jinja2 import Template

func_template = """load("@rules_java//java:defs.bzl", "java_library")

{% for comp in comps %}
java_library(
    name = "{{ comp.name }}_lib",
    srcs = [
        {% for src in comp.srcs %}
        "{{ src }}",
        {% endfor %}
    ],
    deps = [
        {% for dep in comp.deps %}
        "{{ dep }}",
        {% endfor %}
    ],
    visibility = ["//visibility:public"],
)
{% endfor %}
"""

wrap_template = """load("@rules_java//java:defs.bzl", "java_binary")

java_binary(
    name = "wrapper_app",
    srcs = ["Wrapper.java"],
    main_class = "wrapper.Wrapper",
    data = ["chain-config.json"],
    deps = [
        {% for dep in deps %}
        "{{ dep }}",
        {% endfor %}
    ],
)
"""

mod_template = """module(
    name = "dynamic_composition_poc",
    version = "1.0",
)

bazel_dep(name = "rules_java", version = "9.0.3")
bazel_dep(name = "rules_jvm_external", version = "5.3")

maven = use_extension("@rules_jvm_external//:extensions.bzl", "maven")
maven.install(
    artifacts = [
        {% for artifact in artifacts %}
        "{{ artifact }}",
        {% endfor %}
    ],
)
use_repo(maven, "maven")
"""

def main():
    funcdir = os.path.join("..", "functions")
    wrapdir = os.path.join("..", "wrapper")
    rootdir = os.path.join("..")

    wrap_deps = ["@maven//:com_google_code_gson_gson"]

    func = Template(func_template)
    wrap = Template(wrap_template)
    mod = Template(mod_template)

    for dir_name in os.listdir(funcdir):
        dir_path = os.path.join(funcdir, dir_name)

        if not os.path.isdir(dir_path):
            continue

        data_path = os.path.join(dir_path, "metadata.json")
        if not os.path.exists(data_path):
            continue

        with open(data_path, "r", encoding="utf-8") as f:
            data = json.load(f)

        comps = data.get("comps", [])

        rendered_func = func.render(comps = comps)
        with open(os.path.join(dir_path, "BUILD"), "w", encoding="utf-8") as f:
            f.write(rendered_func)
        
        for comp in comps:
            wrap_deps.append(f"//functions/{dir_name}:{comp['name']}_lib")

    rendered_wrap=wrap.render(deps = wrap_deps)
    with open(os.path.join(wrapdir, "BUILD"), "w", encoding="utf-8") as f:
        f.write(rendered_wrap)

    #!!
    artifacts = [
        "com.google.code.gson:gson:2.10.1"
    ]

    rendered_mod = mod.render(artifacts = artifacts)
    with open(os.path.join(rootdir, "MODULE.bazel"), "w", encoding="utf-8") as f:
        f.write(rendered_mod)

if __name__ == "__main__":
    main() 

        
