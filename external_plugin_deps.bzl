load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    maven_jar(
        name = "gwtorm",
        artifact = "com.google.gerrit:gwtorm:1.20",
        sha1 = "a4809769b710bc8ce3f203125630b8419f0e58b0",
        src_sha1 = "cb63296276ce3228b2d83a37017a99e38ad8ed42",
    )
