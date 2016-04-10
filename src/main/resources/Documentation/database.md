<link href="../com/googlesource/gerrit/plugins/verifystatus/public/verifystatus.css" rel="stylesheet"></link>

Database
========

DESCRIPTION
-----------
CI data is stored in a [CI database](#supported-dbs) which can be in the Gerrit
review database or a completely separate database.

### <a id="schema-initialization"> @PLUGIN@ schema initialization

Schema initialization
---------------------

The database is initialized and the schema is created with during an initial
site creation.

```
*** SQL Database for @PLUGIN@
*** 

Database server type           [h2]: ?
       Supported options are:
         derby
         h2
         mysql
         oracle
         postgresql
Database server type           [h2]: h2

Initialized <gerrit-site>
```

### <a id="schema-upgrade"> @PLUGIN@ schema upgrade

Schema upgrade
--------------

Schema upgrade takes place in init plugin step:

```
*** SQL Database for @PLUGIN@
*** 

Database server type           [h2]: 

Upgrading schema to 2 ...
Migrating data to schema 2 ...
```

### <a id="configure-db"> @PLUGIN@ configure-db

Configuration
-------------

The @PLUGIN@ database can be configured in the [gerrit.config]
(../../../Documentation/config-gerrit.html#_file_code_etc_gerrit_config_code)
file

The [database section](#database-params) configures where the @PLUGIN@ stores
per patchset CI results.

```
[plugin "@PLUGIN@"]
  dbType = MYSQL
  dbUrl = jdbc:mysql://localhost:3306/cidata
  username = gerrit2
  password = s3kr3t
```

### <a id="supported-dbs"> @PLUGIN@ supported databases
 * H2
 * Apache Derby
 * MySQL
 * Oracle
 * PostgreSQL

### <a id="database-params"> Database Parameters

|Paramter|    |Description|
|:-------|:---|:----------|
|database.dbType|required|Type of database server to connect to (default: H2)|
|database.username|required|Username to connect to the database server as|
|database.password|required|Password to authenticate to the database server with|
|database.url|required|'jdbc:' URL for the database|
|database.driver|optional|Name of the JDBC driver class to connect to the database with|
|database.connectionPool|optional|If true, use connection pooling for database connections|
|database.poolLimit|optional|Maximum number of open database connections|
|database.poolMinIdle|optional|Minimum number of connections to keep idle in the pool|
|database.poolMaxIdle|optional|Maximum number of connections to keep idle in the pool|
|database.poolMaxWait|optional|Maximum amount of time a request processing thread will wait to acquire a database connection from the pool|
|database.dataSourceInterceptorClass|optional|Class that implements DataSourceInterceptor interface to monitor SQL activity|

* ms, milliseconds
* s, sec, second, seconds
* m, min, minute, minutes
* h, hr, hour, hours

If a unit suffix is not specified, `milliseconds` is assumed.

Default is `30 seconds`.


### <a id="access-database"> @PLUGIN@ access-database

Acccesing Database
------------------

@PLUGIN@ provides an administrative interface to the database.

'ssh' -p <port> <host> '@PLUGIN@ gsql' [--format {PRETTY | JSON | JSON_SINGLE}] [-c QUERY]


> ssh -p 29418 localhost verify-status gsql -c "'show tables;'"


SEE ALSO
--------

* [Database Setup](../../../Documentation/database-setup.html)
* [Automatic Site Initialization](../../../Documentation/config-auto-site-initialization.html)
* [Database Settings](../../../Documentation/config-gerrit.html#database)
* [Gerrit gsql Command](../../../Documentation/cmd-gsql.html)


GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
