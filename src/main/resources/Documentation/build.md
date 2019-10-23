@PLUGIN@ Build
==============

This plugin can be built with Bazel.

Bazel
----

Two build modes are supported: Standalone and in Gerrit tree.
The standalone build mode is recommended, as this mode doesn't require
the Gerrit tree to exist locally.

### Build standalone

To build the plugin, issue the following command:

```
  bazel build verify-status
```

The output is created in

```
  bazel-bin/@PLUGIN@.jar
```

To execute the tests run:

```
  bazel test verify-status_tests
```

### Build in Gerrit tree

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  bazel build plugins/@PLUGIN@
```

The output is created in

```
  bazel-bin/plugins/@PLUGIN@/@PLUGIN@.jar
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
  ./tools/eclipse/project_bzl.py
```

To execute the tests run:

```
  bazel test plugins/verify-status:verify-status_tests
```

How to build the Gerrit Plugin API is described in the [Gerrit
documentation](../../../Documentation/dev-bazel.html#_extension_and_plugin_api_jar_files).
