workspace(name = "verify_status")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "fcd446d3969b7f3f77bd509ffe5c20c7dbcab542",
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
