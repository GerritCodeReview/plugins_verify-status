@PLUGIN@ Build
==============

This plugin can be built with Bazel or Maven.

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
  bazel-genfiles/@PLUGIN@.jar
```

To execute the tests run:

```
  bazel test verify_status_tests
```

### Build in Gerrit tree

Clone or link this plugin to the plugins directory of Gerrit's source
tree, and issue the command:

```
  bazel build plugins/@PLUGIN@
```

The output is created in

```
  bazel-genfiles/plugins/@PLUGIN@/@PLUGIN@.jar
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
  bazel test plugins/verify-status:verify_status_tests
```

Maven
-----

Note that for compatibility reasons a Maven build is provided, but is considered
to be deprecated and will be removed in a future version of this plugin.

To build with Maven, change directory to the plugin folder and issue the
command:

```
  mvn clean package
```

When building with Maven, the Gerrit Plugin API must be available.

How to build the Gerrit Plugin API is described in the [Gerrit
documentation](../../../Documentation/dev-bazel.html#_extension_and_plugin_api_jar_files).
