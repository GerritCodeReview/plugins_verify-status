load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "gerrit_plugin",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
)

gerrit_plugin(
    name = "verify-status",
    srcs = glob(["src/main/java/**/*.java"]),
    gwt_module = "com.googlesource.gerrit.plugins.verifystatus.VerifyStatusForm",
    manifest_entries = [
        "Gerrit-PluginName: verify-status",
        "Gerrit-Module: com.googlesource.gerrit.plugins.verifystatus.GlobalModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.verifystatus.HttpModule",
        "Gerrit-SshModule: com.googlesource.gerrit.plugins.verifystatus.SshModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.verifystatus.init.InitPlugin",
        "Implementation-Title: Verify Status Plugin",
        "Implementation-URL: https://gerrit-review.googlesource.com/#/admin/projects/plugins/verify-status",
    ],
    provided_deps = [
        "@commons_dbcp//jar:neverlink",
    ],
    resources = glob(["src/main/**/*"]),
)

junit_tests(
    name = "verify_status_tests",
    size = "small",
    srcs = glob(["src/test/java/**/*IT.java"]),
    tags = ["verify-status"],
    deps = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":verify-status__plugin",
    ],
)
