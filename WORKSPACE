workspace(name = "verify_status")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "b07aa8462f2618a83f3b52d6150faa831b24d3e3",
    #local_path = "/home/<user>/projects/bazlets",
)

# Snapshot Plugin API
#load(
#    "@com_googlesource_gerrit_bazlets//:gerrit_api_maven_local.bzl",
#    "gerrit_api_maven_local",
#)

# Release Plugin API
load(
    "@com_googlesource_gerrit_bazlets//:gerrit_api.bzl",
    "gerrit_api",
)
load(
    "@com_googlesource_gerrit_bazlets//:gerrit_gwt.bzl",
    "gerrit_gwt",
)

# Load release Plugin API
gerrit_api()

# Load snapshot Plugin API
#gerrit_api_maven_local()

gerrit_gwt()

load("@com_googlesource_gerrit_bazlets//tools:maven_jar.bzl", "maven_jar")
