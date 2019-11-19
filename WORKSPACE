workspace(name = "verify_status")

load("//:bazlets.bzl", "load_bazlets")

load_bazlets(
    commit = "d100b6aad6b37e7db8fa141020c882dc97fb7723",
    #local_path = "/home/<user>/projects/bazlets",
)

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
http_archive(
    name = "io_bazel_rules_closure",
    sha256 = "a80acb69c63d5f6437b099c111480a4493bad4592015af2127a2f49fb7512d8d",
    strip_prefix = "rules_closure-0.7.0",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/rules_closure/archive/0.7.0.tar.gz",
        "https://github.com/bazelbuild/rules_closure/archive/0.7.0.tar.gz",
    ],

)
load("@io_bazel_rules_closure//closure:defs.bzl", "closure_repositories")

closure_repositories()

# Polymer dependencies
load(
    "@com_googlesource_gerrit_bazlets//:gerrit_polymer.bzl",
    "gerrit_polymer",
)
gerrit_polymer()



# Load Gerrit npm_binary toolchain
load("@com_googlesource_gerrit_bazlets//tools:js.bzl", "GERRIT", "npm_binary")
npm_binary(
    name = "polymer-bundler",
    repository = GERRIT,
)
npm_binary(
    name = "crisper",
    repository = GERRIT,
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

# Load release Plugin API
gerrit_api()

# Load snapshot Plugin API
#gerrit_api_maven_local()


load("@com_googlesource_gerrit_bazlets//tools:maven_jar.bzl", "maven_jar")
