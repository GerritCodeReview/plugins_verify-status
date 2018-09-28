load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
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
    resources = glob(["src/main/**/*"]),
)

java_test(
    name = "verify-status-tests",
    size = "small",
    srcs = ["src/test/java/com/googlesource/gerrit/plugins/verifystatus/VerifyStatusIT.java"],
    tags = ["verify-status"],
    test_class = "com.googlesource.gerrit.plugins.verifystatus.VerifyStatusIT",
    deps = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":verify-status__plugin",
    ],
)
