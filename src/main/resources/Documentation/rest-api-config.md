@PLUGIN@ - /config/ REST API
============================

This page describes the '/config/' REST endpoints that are added by the
@PLUGIN@ plugin.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="project-endpoints"> @PLUGIN@ Endpoints
--------------------------------------------

### <a id="get-config"> Get Config
_GET /config/server/@PLUGIN@~config_

Gets the configuration of the @PLUGIN@ plugin.

#### Request

```
  GET /config/server/@PLUGIN@~config HTTP/1.0
```

As response a [ConfigInfo](#config-info) entity is returned that
contains the configuration of the @PLUGIN@ plugin.

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8

  )]}'
  {
    "show_jobs_panel": true,
    "show_jobs_drop_down_panel": false
  }
```

### <a id="put-config"> Put Config
_PUT /config/server/@PLUGIN@~config_

Sets the configuration of the @PLUGIN@ plugin.

The new configuration must be specified as a [ConfigInfo](#config-info)
entity in the request body. Not setting a parameter leaves the
parameter unchanged.

#### Request

```
  PUT /config/server/@PLUGIN@~config HTTP/1.0
  Content-Type: application/json;charset=UTF-8

  {
    "show_jobs_panel": true,
    "show_jobs_drop_down_panel": false
  }
```

<a id="json-entities">JSON Entities
-----------------------------------

### <a id="config-info"></a>ConfigInfo

The `ConfigInfo` entity contains the configuration of the @PLUGIN@
plugin.

|Field Name               |Description|
|:------------------------|:----------|
|show_jobs_panel          | Whether jobs panel should be displayed|
|show_jobs_drop_down_panel| Whether jobs drop down panel should be displayed|
|show_jobs_summary_panel  | Whether jobs summary panel should be displayed|
|sort_jobs_panel          | The order of jobs sorting on jobs panel (REPORTER|NAME|DATE)|
|sort_jobs_drop_down_panel| The order of jobs sorting on jobs drop down panel (REPORTER|NAME|DATE)|


SEE ALSO
--------

* [Config related REST endpoints](../../../Documentation/rest-api-config.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
