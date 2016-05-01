@PLUGIN@ - /accounts/ REST API
==============================

This page describes the '/accounts/' REST endpoints that are added by
the @PLUGIN@ plugin.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="project-endpoints"> @PLUGIN@ Endpoints
--------------------------------------------

### <a id="get-preferences"> Get Preferences
__GET__ /accounts/{account-id}/@PLUGIN@~preferences

Gets the preferences of a user for the @PLUGIN@ plugin.

#### Request

```
  GET /accounts/self/@PLUGIN@~preferences HTTP/1.0
```

As response a [PreferencesInfo](#preferences-info) entity is returned
that contains the preferences of a user for the @PLUGIN@ plugin.

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json;charset=UTF-8

  )]}'
  {
    "visible_jobs": "ALL"
  }
```

### <a id="put-preferences"> Put Preferences
__PUT__ /accounts/{account-id}/@PLUGIN@~preferences

Sets the user preferences for the @PLUGIN@ plugin.

The new preferences must be specified as a [PreferenceInfo](#preferences-info)
entity in the request body. Not setting a parameter means that the
parameters are set to the defaults.

#### Request

```
  PUT /accounts/self/@PLUGIN@~preferences HTTP/1.0
  Content-Type: application/json;charset=UTF-8

  {
    "visible_jobs": "NONE"
  }
```


<a id="json-entities">JSON Entities
-----------------------------------

### <a id="preferences-info"></a>PreferencesInfo

The `PreferencesInfo` entity contains the preferences of the @PLUGIN@
plugin.

* _visible_jobs_: The control that should be displayed on preferences screen
  to change the number of visible job results below the change info section.
  Possible values are `ALL`, `NONE`, `FIVE`, `TEN`.  `ALL` is the default.

SEE ALSO
--------

* [Account related REST endpoints](../../../Documentation/rest-api-accounts.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
