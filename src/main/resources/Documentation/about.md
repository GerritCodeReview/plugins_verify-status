The @PLUGIN@ plugin allows CI system to report build and test results back to
Gerrit. The reports are stored per patchset and are saved onto an external
database.  Included with this plugin are a set of SSH and REST APIs to automate
the reporting of test results.  This plugin will also handle displaying of the
job results on the Gerrit UI.


### <a id="workflow"></a>
### `Workflow`

A typical workflow for @PLUGIN@ plugin:

1. CI system triggers on a new patchset.
2. CI system executes build jobs.
3. CI system reports build job results with @PLUGIN@ [ssh command](cmd-save.html)
or [rest-api](rest-api-changes.md).
4. CI system reports a combined `Verfiied` vote based on the results of each job
using the review [ssh command](../../../Documentation/cmd-review.html) or
[rest-api](../../../Documentation/rest-api-changes.html#set-review).
5. Users can view per patch job results on Gerrit UI or retrieve the results
using the @PLUGIN@ rest api.


### <a id="change-screen"></a>
### `Change Screen`
Visualized based on the [job results](#job-results) info

![PreferencesScreenshot](images/job_results.png)



### <a id="job-results"></a>
### `Job Results`

The job score (or value) is the result from the executed build.  This
score is independent of the Gerrit label (i.e. `Verified`) score. The
reporter scores each build job and then (if given permission) scores the
combined Verified vote.

|Score          |Result  |
|:------------- |:-------|
|less than 0    |Failed  |
|0              |Unstable|
|greater than 0 |Passed  |


The information icon is an indicator that a job has abstained from voting
(or is a non-voting job).  Abstaining typically indicates that a job's
score may not factor into determining the combined vote.


### <a id="configure-panels"> @PLUGIN@ configure-panels

Configuration
-------------

The @PLUGIN@ job panels can be configured in the [gerrit.config]
(../../../Documentation/config-gerrit.html#_file_code_etc_gerrit_config_code)
file.

#### Parameters

|Field Name             |Description|
|:----------------------|:----------|
|showJobsPanel          | Whether jobs panel should be displayed (default to true)|
|showJobsDropDownPanel  | Whether jobs drop down panel should be displayed (default to true)|


#### Example

```
[plugin "@PLUGIN@"]
   showJobsPanel = false
   showJobsDropDownPanel = false
```

