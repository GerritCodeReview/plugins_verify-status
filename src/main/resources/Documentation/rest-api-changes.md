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
  GET /changes/myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940/revisions/674ac754f91e64a0efb8087e59a176484bd534d1/@PLUGIN@~verifications HTTP/1.0
```

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json; charset=UTF-8

  )]}'
  {
    "5081c5e5-e101-43eb-8e59-4e197f22a0d0"": {
      "name": "gate-horizon-pep8",
      "url": "https://ci.host.com/jobs/gate-horizon-pep8/2711",
      "value": -1,
      "reporter": "Jenkins Check",
      "comment": "Failed",
      "category": "cloud server",
      "duration": "3m 10s"
      "granted": "15 Mar 2016 08:10:41",
    },
    "2a359a73-31e7-4f81-b295-ae0e20615da6": {
      "name": "gate-horizon-python27",
      "url": "https://ci.host.com/jobs/gate-horizon-python27/1711",
      "value": 1,
      "abstain": true,
      "reporter": "Acme CI",
      "comment": "Informational only",
      "category": "third party",
      "duration": "7m 40s"
      "granted": "15 Mar 2016 08:30:16"
    }
    "807c8ece-0196-4ec4-b24f-ed035efa8e55": {
      "name": "gate-horizon-python34",
      "url": "https://ci.host.com/jobs/gate-horizon-python34/9111",
      "value": 1,
      "reporter": "Drone CI",
      "comment": "RuntimeError: File was not found",
      "granted": "15 Mar 2016 08:40:23",
      "category": "third party",
      "duration": "12m 20s"
      "granted": "15 Mar 2016 08:40:23",
    }
  }
```

### <a id="verification-options"> Verification Options
Verifications Options

Sort(s)::
Sort the results by a field.

|Field Name |Description|
|:----------|:----------|
|NAME       |Sort job name in ascending order|
|REPORTER   |Sort reporter in ascending order|

*__Note__: Fields are also sorted by the time the job was saved in descending
order. 

#### Request

```
  GET /changes/100/revisions/1/@PLUGIN@~verifications/?sort=REPORTER HTTP/1.0
```


Filter(f)::

|Field Name |Description|
|:----------|:----------|
|CURRENT    |Limit the results to the most current list of reports|
|FAILED     |Limit the results to only failed jobs|

#### Example

Assuming "Jenkins Check" and "ACME CI" published multiple reports to Gerrit.
Retrieve the most current report(s) by each reporter:

#### Request

```
  GET /changes/100/revisions/1/@PLUGIN@~verifications/?sort=REPORTER&filter=CURRENT HTTP/1.0
```

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json; charset=UTF-8

  )]}'
  {
    "2a359a73-31e7-4f81-b295-ae0e20615da6": {
      "name": "gate-horizon-python27",
      "url": "https://ci.host.com/jobs/gate-horizon-python27/1711",
      "value": 1,
      "abstain": true,
      "reporter": "Acme CI",
      "comment": "Informational only",
      "category": "third party",
      "duration": "7m 40s"
      "granted": "15 Mar 2016 08:30:16"
    },
    "5081c5e5-e101-43eb-8e59-4e197f22a0d0"": {
      "name": "gate-horizon-pep8",
      "url": "https://ci.host.com/jobs/gate-horizon-pep8/2711",
      "value": -1,
      "reporter": "Jenkins Check",
      "comment": "Failed",
      "category": "cloud server",
      "duration": "3m 10s"
      "granted": "15 Mar 2016 08:10:41",
    }
  }
```

### <a id="get-stats"> Get Stats

__GET__ /changes/{change-id}/revisions/{revision-id}/@PLUGIN@~stats

Gets the [verification statistics](#verification-stats) for a change.  Please
refer to the general
[changes rest api](../../../Documentation/rest-api-changes.html#get-review)
for additional info on this request.

#### Request

```
  GET /changes/myProject~master~I8473b95934b5732ac55d26311a706c9c2bde9940/revisions/674ac754f91e64a0efb8087e59a176484bd534d1/@PLUGIN@~stats HTTP/1.0
```

#### Response

```
  HTTP/1.1 200 OK
  Content-Disposition: attachment
  Content-Type: application/json; charset=UTF-8

  )]}'
  {
    "passed": 8,
    "failed": 1,
    "unstable": 1,
    "voting": 9
  }
```

Stats Options

Filter(f)::

|Field Name |Description|
|:----------|:----------|
|CURRENT    |Limit the results to the most current list of reports|

#### Example

Get stats for just the most current report:

#### Request

```
  GET /changes/100/revisions/1/@PLUGIN@~stats?filter=CURRENT HTTP/1.0
```


### <a id="post-verify"> Post Verify

__POST__ /changes/{change-id}/revisions/{revision-id}/@PLUGIN@~verifications

Posts a verification result to a patchset. Each verification result is save as
a unique entry in the database identified by a UUID.  Results can be updated by
posting with the UUID.

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
      "abstain": true,
      "reporter": "Jenkins Check",
      "comment": "Informational only",
      "category": "third party",
      "duration": "2m 40s"
    },
    "gate-horizon-python34": {
      "url": "https://ci.host.com/jobs/gate-horizon-python34/1711",
      "value": -1,
      "abstain": false,
      "reporter": "Jenkins Check",
      "comment": "RuntimeError: File was not found",
      "category": "third party",
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
|name        |required|The name of this job|
|value       |required|The pass/fail result for this job|
|abstain     |optional|Whether the value counts as a vote (defaults to false)|
|comment     |optional|A short comment about this job|
|url         |optional|The url link to more info about this job|
|reporter    |optional|The user that verified this job|
|category    |optional|A category for this job|
|duration    |optional|The time it took to run this job|


*__Notice__: There is a special category called 'recheck' that indicates that
the job was a rerun on the same patchset.  An icon will appear on the change
screen for these jobs.

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
|name       |The name of this job|
|value      |The pass/fail result for this job|
|abstain    |Whether the value counts as a vote|
|comment    |A short comment about this job|
|url        |The url link to more info about this job|
|reporter   |The user that verified this job|
|category   |A category for this job|
|duration   |The time it took to run this job|
|granted    |The date this verification was recorded|


### <a id="verification-stats"></a>VerificationStats

The `VerificationStats` entity describes statistics on a patch set.

|Field Name |Description|
|:----------|:----------|
|passed     |The number of passed jobs|
|failed     |The number of failed jobs|
|unstable   |The number of unstable jobs|
|voting     |The number of voting jobs|

ACCESS
------
Caller must be a member of a group that is granted the
'Save Verification Report' capability (provided by this plugin) in order to
POST reports.

SEE ALSO
--------

* [Change related REST endpoints](../../../Documentation/rest-api-changes.html)
* [Plugin Development](../../../Documentation/dev-plugins.html)
* [REST API Development](../../../Documentation/dev-rest-api.html)

GERRIT
------
Part of [Gerrit Code Review](../../../Documentation/index.html)
