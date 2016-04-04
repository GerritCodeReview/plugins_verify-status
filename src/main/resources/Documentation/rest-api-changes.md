<style>
  table{
      border-collapse: collapse;
      border-spacing: 0;
      border:2px solid #000000;
  }
  
  th{
      border:2px solid #000000;
  }
  
  td{
      border:1px solid #000000;
  }
</style>

@PLUGIN@ - /changes/ REST API
==============================

This page describes the '/changes/' REST endpoints that are added by
the @PLUGIN@ plugin.

Please also take note of the general information on the
[REST API](../../../Documentation/rest-api.html).

<a id="plugin-endpoints"> @PLUGIN@ Endpoints
--------------------------------------------

### <a id="get-verifications"> Get Verifications

__GET__ /changes/{change-id}/revisions/{revision-id}/@PLUGIN@~verifications

Gets the [verifications](#verification-info) for a change.  Please refer to the
general [changes rest api](../../../Documentation/rest-api-changes.html#get-review)
for additional info on this request.

#### Request

```
  GET /changes/myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940/revisions/674ac754f91e64a0efb8087e59a176484bd534d1/verifications HTTP/1.0
```

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json; charset=UTF-8

  )]}'
  {
    "gate-horizon-pep8": {
      "url": "https://ci.host.com/jobs/gate-horizon-pep8/1711",
      "value": 1,
      "verifier": "Jenkins",
      "comment": "Non Voting",
      "granted": "15 Mar 2016 08:10:41"
    },
    "gate-horizon-python27": {
      "url": "https://ci.host.com/jobs/gate-horizon-python27/1711",
      "value": 1,
      "verifier": "Jenkins",
      "comment": "Passed",
      "granted": "15 Mar 2016 08:30:16"
    }
    "gate-horizon-python34": {
      "url": "https://ci.host.com/jobs/gate-horizon-python34/1711",
      "value": -1,
      "verifier": "Jenkins",
      "comment": "Failed",
      "granted": "15 Mar 2016 08:40:23"
    }
  }
```

### <a id="post-verify"> Post Verify

__POST__ /changes/{change-id}/revisions/{revision-id}/@PLUGIN@~verifications

Posts a verification result to a patchset.

The verification must be provided in the request body as a
[VerifyInput](#verify-input) entity.

#### Request

```
  POST /changes/myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940/revisions/674ac754f91e64a0efb8087e59a176484bd534d1/verify-status~verifications HTTP/1.0
  Content-Type: application/json;charset=UTF-8

```

#### Example

Post two verification results to patchset revision 14a95001c.
_Notice_ two levels of quoting are required, one for the local shell, and
another for the argument parser inside the Gerrit server.

```
curl -X POST --digest --user joe:secret --data-binary
@verification_data.txt --header "Content-Type: application/json; charset=UTF-8"
http://localhost:8080/a/changes/1000/revisions/14a95001c/verify-status~verifications

$ cat verification_data.txt
{
  "verifications": {
    "gate-horizon-python27": {
      "url": "https://ci.host.com/jobs/gate-horizon-python27/1711",
      "value": 1,
      "verifier": "Jenkins",
      "comment": "Passed"
    },
    "gate-horizon-python34": {
      "url": "https://ci.host.com/jobs/gate-horizon-python34/1711",
      "value": -1,
      "verifier": "Jenkins",
      "comment": "Failed"
    }
  }
}

```

<a id="json-entities">JSON Entities
-----------------------------------

### <a id="verify-input"></a>VerifyInput

The `VerifyInput` entity contains information for adding a verification
to a revision.


|Field Name  |     |Description|
|:-----------|:----|:----------|
|category    |required|The name of the category to be added as a verification|
|value       |required|The value associated with the category|
|comment     |optional|The comment associated with the category|
|url         |optional|The url associated with the category|
|verifier    |optional|The user that verified the revision|



### <a id="revision-info"></a>RevisionInfo

The `RevisionInfo` entity contains information about a patch set.
Not all fields are returned by default.  Additional fields can
be obtained by adding `o` parameters as described in
[Query Changes](../../../Documentation/rest-api-changes.html#list-changes)

|Field Name    |    |Description |
|:-------------|:---|:-----------|
|verifications |optional|The verifications on the patchset as a list of `VerificationInfo` entities|


### <a id="verification-info"></a>VerificationInfo

The `VerificationInfo` entity describes a verification on a patch set.

|Field Name |Description|
|:----------|:----------|
|comment    |A short comment about about this verification|
|url        |The URL for this verification|
|value      |The value for this verification|
|verifier   |The user that reported this verification|



SEE ALSO
--------

* [Change related REST endpoints](../../../Documentation/rest-api-changes.html)
* [Plugin Development](../../../Documentation/dev-plugins.html)
* [REST API Development](../../../Documentation/dev-rest-api.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
