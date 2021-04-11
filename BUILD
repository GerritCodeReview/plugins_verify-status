load("@npm//@bazel/rollup:index.bzl", "rollup_bundle")
load("@rules_java//java:defs.bzl", "java_library")
load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)
load("//tools/bzl:junit.bzl", "junit_tests")
load("//tools/bzl:genrule2.bzl", "genrule2")
load("//tools/bzl:js.bzl", "polygerrit_plugin")

gerrit_plugin(
    name = "verify-status",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: verify-status",
        "Gerrit-Module: com.googlesource.gerrit.plugins.verifystatus.GlobalModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.verifystatus.HttpModule",
        "Gerrit-SshModule: com.googlesource.gerrit.plugins.verifystatus.SshModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.verifystatus.init.InitPlugin",
        "Implementation-Title: Verify Status Plugin",
        "Implementation-URL: https://gerrit-review.googlesource.com/#/admin/projects/plugins/verify-status",
    ],
    resource_jars = [":gr-verify-status-static"],
    resources = glob(["src/main/resources/**/*"]),
    deps = ["@gwtorm//jar"],
)

junit_tests(
    name = "verify-status_tests",
    srcs = ["src/test/java/com/googlesource/gerrit/plugins/verifystatus/VerifyStatusIT.java"],
    tags = ["verify-status"],
    deps = [
        ":verify-status__plugin_test_deps",
        "@gwtorm//jar",
    ],
)

java_library(
    name = "verify-status__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":verify-status__plugin",
    ],
)

genrule2(
    name = "gr-verify-status-static",
    srcs = [":gr-verify-status"],
    outs = ["gr-verify-status-static.jar"],
    cmd = " && ".join([
        "mkdir $$TMP/static",
        "cp -r $(locations :gr-verify-status) $$TMP/static",
        "cd $$TMP",
        "zip -Drq $$ROOT/$@ -g .",
    ]),
)

polygerrit_plugin(
    name = "gr-verify-status",
    app = "gr-verify-status-bundle.js",
    plugin_name = "gr-verify-status",
)

rollup_bundle(
    name = "gr-verify-status-bundle",
    srcs = glob(["gr-verify-status/*.js"]),
    entry_point = "gr-verify-status/plugin.js",
    format = "iife",
    rollup_bin = "//tools/node_tools:rollup-bin",
    sourcemap = "hidden",
    deps = [
        "@tools_npm//rollup-plugin-node-resolve",
    ],
)
