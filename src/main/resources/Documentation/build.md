@PLUGIN@ Build
==============

This plugin can be built with Bazel.

Bazel
----

To build in Gerrit tree clone or link this plugin to the plugins
directory of Gerrit's source tree.

Put the external dependency Bazel build file into the Gerrit /plugins
directory, replacing the existing empty one.

```
  cd gerrit/plugins
  rm external_plugin_deps.bzl
  ln -s @PLUGIN@/external_plugin_deps.bzl .
```

From Gerrit source tree issue the command:

```
  bazel build plugins/@PLUGIN@
```

The output is created in

```
  bazel-bin/plugins/@PLUGIN@/@PLUGIN@.jar
```

To execute the tests run either one of:

```
  bazel test --test_tag_filters=@PLUGIN@ //...
  bazel test plugins/@PLUGIN@:@PLUGIN@_tests
```

This project can be imported into the Eclipse IDE. List the plugin in the
custom plugin list, in `gerrit/tools/bzl/plugins.bzl`:

```
CUSTOM_PLUGINS = [
  [...]
  'verify-status',
]
```

and issue the command:

```
  ./tools/eclipse/project.py
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
