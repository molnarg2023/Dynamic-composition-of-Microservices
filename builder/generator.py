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
load("@rules_oci//oci:defs.bzl", "oci_image", "oci_tarball")
load("@rules_pkg//pkg:tar.bzl", "pkg_tar")

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

pkg_tar(
    name = "app_layer",
    srcs = [
        ":wrapper_app_deploy.jar",
    ],
    package_dir = "/app",
)

pkg_tar(
    name = "config_layer",
    srcs = [
        "chain-config.json",
    ],
    package_dir = "/app/wrapper",
)

oci_image(
    name = "image",
    base = "@alpine_java_base",
    tars = [
        ":app_layer",
        ":config_layer",
    ],
    entrypoint = ["java", "-jar", "/app/wrapper_app_deploy.jar", "/app/wrapper/chain-config.json"],
    workdir = "/app",
)

oci_tarball(
    name = "tarball",
    image = ":image",
    repo_tags = ["{{ repo_tag }}"],
)
"""

mod_template = """module(
    name = "dynamic_composition_poc",
    version = "1.0",
)

bazel_dep(name = "rules_java", version = "8.6.3")
bazel_dep(name = "rules_jvm_external", version = "6.7") 

maven = use_extension("@rules_jvm_external//:extensions.bzl", "maven")
maven.install(
    artifacts = [
        {% for artifact in artifacts %}
        "{{ artifact }}",
        {% endfor %}
    ],
)
use_repo(maven, "maven")

bazel_dep(name = "rules_oci", version = "1.8.0")
bazel_dep(name = "rules_pkg", version = "1.0.1")

bazel_dep(name = "platforms", version = "1.0.0")
bazel_dep(name = "apple_support", version = "1.24.2")
bazel_dep(name = "aspect_bazel_lib", version = "2.13.0")

oci = use_extension("@rules_oci//oci:extensions.bzl", "oci")

oci.pull(
    name = "alpine_java_base",
    image = "gcr.io/distroless/java21-debian12",
    digest = "sha256:f34fd3e4e2d7a246d764d0614f5e6ffb3a735930723fac4cfc25a72798950262",
    platforms = [
        "linux/arm64/v8",
    ],
)

use_repo(oci, "alpine_java_base")
"""

def main():
    funcdir = os.path.join("..", "functions")
    wrapdir = os.path.join("..", "wrapper")
    rootdir = os.path.join("..")

    wrap_deps = ["@maven//:com_google_code_gson_gson"]

    #!!
    partition_name = "partition-1"
    repo_tag = f"onlab/{partition_name}:latest"

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

    rendered_wrap=wrap.render(deps = wrap_deps, repo_tag = repo_tag)
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

        
