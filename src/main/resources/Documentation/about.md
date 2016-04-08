<link href="../com/googlesource/gerrit/plugins/verifystatus/public/verifystatus.css" rel="stylesheet"></link>

The @PLUGIN@ plugin allows CI system to report build and test results back to
Gerrit. The reports are stored per patchset and are saved onto an external
database.  Included with this plugin are a set of SSH and REST APIs to automate
the reporting of test results.  This plugin will also handle displaying of the
job results on the Gerrit UI.


### `Change Screen`
Visualized based on the [job results](#job-results) mapping

![PreferencesScreenshot](images/job_results.png)



### <a id="job-results"></a>JobResults
### `Job Results`

Job result scores represent the results from the executed build.  These
scores are independent of the Gerrit label (i.e. Verified) score.  A job
result score can be classified as `voting` or `non-voting`.  `Voting`
means that the job result vote will count towards the Gerrit label
vote, which we call the combined vote.  The combined vote is an aggregate
of all of the job result votes.  For example, if a patchset has 10 build jobs
and if any one of those jobs scored -2 then it should result in a combined
`Verfied-1` vote.


|Job Score |Job Result      |Vote Count?
|:---------|:---------------|:----------
|-2        |Failed          |Voting
|-1        |Failed          |Non-Voting
| 0        |Warning         |Non-Voting
|+1        |Passed          |Non-Voting
|+2        |Passed          |Voting
