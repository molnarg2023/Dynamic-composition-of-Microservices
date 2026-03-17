load("@rules_java//java:defs.bzl", "java_binary")

java_binary(
    name = "wrapper_app",
    srcs = ["Wrapper.java"],
    main_class = "wrapper.Wrapper",
    data = ["chain-config.json"],
    deps = [
        "//functions/f1:f1_lib",
        "//functions/f2:f2_lib",
        "//functions/f3:f3_lib",
        "//functions/f4:f4_lib",
        "@maven//:com_google_code_gson_gson",
    ],
)