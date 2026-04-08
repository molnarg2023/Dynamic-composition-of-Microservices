load("@rules_java//java:defs.bzl", "java_binary")
load("@rules_oci//oci:defs.bzl", "oci_image", "oci_tarball")
load("@rules_pkg//pkg:tar.bzl", "pkg_tar")

java_binary(
    name = "wrapper_app",
    srcs = ["Wrapper.java"],
    main_class = "wrapper.Wrapper",
    data = ["chain-config.json"],
    deps = [
        
        "@maven//:com_google_code_gson_gson",
        
        "//functions/f4:f4_lib",
        
        "//functions/f3:f3_lib",
        
        "//functions/f2:f2_lib",
        
        "//functions/f1:f1_lib",
        
        "//functions/f1:f1Helper_lib",
        
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
    repo_tags = ["onlab/partition-1:latest"],
)