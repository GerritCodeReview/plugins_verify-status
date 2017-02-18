# MySql example

This is an example of how to install a fresh Gerrit with verify-status plugin.

Create initial site (without verify-status)
-------------------------------------------

```
  java -jar gerrit-stable-2.13.war init --batch --no-auto-start --install-all-plugins -d mysite
```

  *NOTE - This will create an H2 db for gerrit.


Install verify status plugin
----------------------------

Copy verify-status.jar to mysite/plugins folder


Setup MySql DB
--------------

```
CREATE USER 'gerrit2'@'localhost' IDENTIFIED BY 'secret';
create database reviewdb;
GRANT ALL ON reviewdb.* TO 'gerrit2'@'localhost';
create database cidata;
GRANT ALL ON cidata.* TO 'gerrit2'@'localhost';
FLUSH PRIVILEGES;
```

Update gerrit config
--------------------

Add the following to etc/gerrit.config file

```
[database]
  type = mysql
  database = reviewdb
  username = gerrit2
  password = secret
  hostname = localhost

[plugin "verify-status"]
  dbType = mysql
  database = cidata
  username = gerrit2
  password = secret
  hostname = localhost
```

Delete git repos
----------------

*NOTE* - only do this if you've change reviewdb from H2 to mysql otherwise you may get the following error

```
 Exception in thread "main" com.google.gwtorm.server.OrmException: Cannot initialize schema
	at com.google.gerrit.server.schema.SchemaUpdater.update(SchemaUpdater.java:104)
	at com.google.gerrit.pgm.init.BaseInit$SiteRun.upgradeSchema(BaseInit.java:367)
	at com.google.gerrit.pgm.init.BaseInit.run(BaseInit.java:133)
	at com.google.gerrit.pgm.util.AbstractProgram.main(AbstractProgram.java:64)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at com.google.gerrit.launcher.GerritLauncher.invokeProgram(GerritLauncher.java:163)
	at com.google.gerrit.launcher.GerritLauncher.mainImpl(GerritLauncher.java:104)
	at com.google.gerrit.launcher.GerritLauncher.main(GerritLauncher.java:59)
	at Main.main(Main.java:25)
 Caused by: java.io.IOException: Cannot update refs/meta/config in /Users/zaro0508/work-gerrit/gerrit-213/mysite/git/All-Projects.git: LOCK_FAILURE
	at com.google.gerrit.server.git.VersionedMetaData$1.updateRef(VersionedMetaData.java:436)
	at com.google.gerrit.server.git.VersionedMetaData$1.createRef(VersionedMetaData.java:335)
	at com.google.gerrit.server.git.VersionedMetaData.commitToNewRef(VersionedMetaData.java:217)
	at com.google.gerrit.server.schema.AllProjectsCreator.initAllProjects(AllProjectsCreator.java:180)
	at com.google.gerrit.server.schema.AllProjectsCreator.create(AllProjectsCreator.java:100)
	at com.google.gerrit.server.schema.SchemaCreator.create(SchemaCreator.java:86)
	at com.google.gerrit.server.schema.SchemaUpdater.update(SchemaUpdater.java:102)
	... 11 more
```

The 1st run of init syncs the git repos to H2 DB.  Now we want to setup git repos with mysql reviewdb.  We are setting
up a new reviewdb this time so we need to sync with new git repos. Delete and next step will create new repos.

```
 rm -rf mysite/git/*
```

Setup Db tables for verify status
----------------------------------

Rerun init to setup tables for reviewdb and cidata

```
 java -jar gerrit-stable-2.13.war init --batch --no-auto-start --install-all-plugins -d mysite
```
