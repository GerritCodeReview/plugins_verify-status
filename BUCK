include_defs('//bucklets/gerrit_plugin.bucklet')

MODULE = 'com.googlesource.gerrit.plugins.verify.CiForm'

gerrit_plugin(
  name = 'verify-status',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/**/*']),
  gwt_module = MODULE,
  manifest_entries = [
    'Gerrit-PluginName: verify',
    'Gerrit-Module: com.googlesource.gerrit.plugins.verify.GlobalModule',
    'Gerrit-HttpModule: com.googlesource.gerrit.plugins.verify.HttpModule',
    'Gerrit-SshModule: com.googlesource.gerrit.plugins.verify.SshModule',
    'Gerrit-InitStep: com.googlesource.gerrit.plugins.verify.init.InitPlugin',
    'Implementation-Title: Verify Status Plugin',
    'Implementation-URL: https://gerrit-review.googlesource.com/#/admin/projects/plugins/verify-status',
  ],
  provided_deps = [
    '//lib/commons:dbcp',
    '//lib:gson',
  ]
)

java_test(
  name = 'verify-status_tests',
  srcs = glob(['src/test/java/**/*IT.java']),
  labels = ['verify-status'],
  source_under_test = [':verify-status__plugin'],
  deps = GERRIT_PLUGIN_API + GERRIT_TESTS + [
    ':verify-status__plugin',
  ],
)

java_library(
  name = 'classpath',
  deps = GERRIT_PLUGIN_API + GERRIT_TESTS + [
    ':verify-status__plugin'
  ],
)
