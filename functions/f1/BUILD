load("@rules_java//java:defs.bzl", "java_library")


java_library(
    name = "f1_lib",
    srcs = [
        
        "f1.java",
        
    ],
    deps = [
        
        "@maven//:com_google_code_gson_gson",
        
        ":f1Helper_lib",
        
    ],
    visibility = ["//visibility:public"],
)

java_library(
    name = "f1Helper_lib",
    srcs = [
        
        "f1Helper.java",
        
    ],
    deps = [
        
    ],
    visibility = ["//visibility:public"],
)
