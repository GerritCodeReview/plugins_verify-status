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
    "check-horizon-pep8": {
      "url": "https://ci.host.com/jobs/gate-horizon-pep8/1711",
      "value": 1,
      "reporter": "Jenkins CI",
      "comment": "Experimental",
      "granted": "15 Mar 2016 08:10:41",
      "category": "check",
      "duration": "1m 30s"
    },
    "gate-horizon-python27": {
      "url": "https://ci.host.com/jobs/gate-horizon-python27/1711",
      "value": 1,
      "reporter": "Acme CI",
      "comment": "Passed",
      "granted": "15 Mar 2016 08:30:16"
      "category": "gate",
      "duration": "2m 40s",
    }
    "gate-horizon-python34": {
      "url": "https://ci.host.com/jobs/gate-horizon-python34/1711",
      "value": -1,
      "reporter": "Drone CI",
      "comment": "Failed",
      "granted": "15 Mar 2016 08:40:23",
      "category": "gate",
      "duration": "12m 20s"
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
      "reporter": "Acme CI",
      "comment": "Passed",
      "category": "gate",
      "duration": "2m 40s"
    },
    "gate-horizon-python34": {
      "url": "https://ci.host.com/jobs/gate-horizon-python34/1711",
      "value": -1,
      "reporter": "Drone CI",
      "comment": "Failed",
      "category": "gate",
      "duration": "12m 20s"
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
|value       |required|The vote for this job|
|comment     |optional|A short comment about this job|
|url         |optional|The url link to more info about this job|
|reporter    |optional|The user that verified this job|
|category    |optional|A category for this job|
|duration    |optional|The time it took to run this job|


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
|value      |The vote for this job|
|comment    |A short comment about this job|
|url        |The url link to more info about this job|
|reporter   |The user that verified this job|
|category   |A category for this job|
|duration   |The time it took to run this job|
|granted    |The date this verification was recorded|



SEE ALSO
--------

* [Change related REST endpoints](../../../Documentation/rest-api-changes.html)
* [Plugin Development](../../../Documentation/dev-plugins.html)
* [REST API Development](../../../Documentation/dev-rest-api.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
