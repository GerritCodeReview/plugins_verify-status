# Gerrit Verify Status Plugin

A typical Gerrit installation contains integration with an automated
testing system that evaluates patchsets and reports results to Gerrit.
The only way for a Continous Integration system to report results to
Gerrit is by posting a review as a comment. The problem with this
workflow is that automated reviews and human reviews are stored as
one piece of data (comments). Human reviews are inherently different
than automated reviews. Human reviews have more meaning to other human
reviewers, it serves as a conversation between people that are reviewing
the change and thus it is typically given higher priority over automated
reviews. Comments provide a great forum to discuss a change however when
robots clutter that forum it overwhelms human reviewers and thus impede
the discussion. Robots should have a separate feedback channel so that
the data can be easily queried, viewed and analyzed independently from
human comments.

This is where the verify-status plugin may help. It creates a separate
“verify-status” channel for automated system to report test results.
It provides a set of SSH commands and REST endpoints allowing easy
integration with any CI system. It allows the verify-status data to be
stored in the Gerrit database or on a completely separate database.
It provides a set of UI components to view the data independent of
Gerrit comments. Lastly there's even a Jenkins plugin (Gerrit verify
status reporter) that will publish test results to gerrit using this
new communications channel.

More information about this plugin can be found in the documentation.
Additionally the Gerrit verify status reporter plugin provides a
[quick start guide] that has a complete set of instruction on
how to integrate Gerrit verify status with Jenkins.

[quick start guide]: https://github.com/jenkinsci/gerrit-verify-status-reporter-plugin/blob/master/README.md
