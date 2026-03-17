load("@rules_java//java:defs.bzl", "java_library")
load("@rules_java//java:defs.bzl", "java_binary")

java_library(
    name = "f1_lib",
    srcs = ["CameraFunc.java"],
    deps = [
        "@maven//:com_google_code_gson_gson",
    ],
    visibility = ["//visibility:public"],
)

java_binary(
    name = "camera_standalone",
    srcs = ["CameraFunc.java"],
    main_class = "functions.f1.CameraFunc",
    deps = [
        "@maven//:com_google_code_gson_gson",
    ],
)