// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.verifystatus;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gerrit.common.errors.PermissionDeniedException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.extensions.api.access.PluginPermission;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.IdString;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.reviewdb.client.PatchSet;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.change.ChangesCollection;
import com.google.gerrit.server.change.RevisionResource;
import com.google.gerrit.server.change.Revisions;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.gerrit.server.project.ProjectControl;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.gerrit.sshd.commands.PatchSetParser;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.googlesource.gerrit.plugins.verifystatus.common.VerificationInfo;
import com.googlesource.gerrit.plugins.verifystatus.common.VerifyInput;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiresCapability(SaveReportCapability.ID)
@CommandMetaData(name = "save", description = "Save patchset verification data")
public class SaveCommand extends SshCommand {
  private static final Logger log =
      LoggerFactory.getLogger(SaveCommand.class);

  private final Set<PatchSet> patchSets = new HashSet<>();
  private final String pluginName;
  private final Provider<CurrentUser> userProvider;
  private final PermissionBackend permissionBackend;

  @Inject
  SaveCommand(@PluginName String pluginName,
      Provider<CurrentUser> userProvider,
      PermissionBackend permissionBackend) {
    this.pluginName = pluginName;
    this.userProvider = userProvider;
    this.permissionBackend = permissionBackend;
  }

  @Argument(index = 0, required = true, multiValued = true,
      metaVar = "{COMMIT | CHANGE,PATCHSET}",
      usage = "list of commits or patch sets to verify")
  void addPatchSetId(String token) {
    try {
      PatchSet ps = psParser.parsePatchSet(token, projectControl,
          branch);
      patchSets.add(ps);
    } catch (UnloggedFailure e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    } catch (OrmException e) {
      throw new IllegalArgumentException("database error", e);
    }
  }

  @Option(name = "--project", aliases = "-p",
      usage = "project containing the specified patch set(s)")
  private ProjectControl projectControl;

  @Option(name = "--branch", aliases = "-b",
      usage = "branch containing the specified patch set(s)")
  private String branch;

  @Option(name = "--verification", aliases = "-v",
      usage = "verification to set the result for", metaVar = "VERIFY=OUTCOME")
  void addJob(String token) {
    parseWithEquals(token);
  }

  private void parseWithEquals(String text) {
    log.debug("processing verification: " + text);
    checkArgument(!Strings.isNullOrEmpty(text), "Empty verification data");
    Map<String, String> params = null;
    try {
      params = Splitter.on("|").withKeyValueSeparator("=").split(text);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(String.valueOf("Invalid verification parameters"));
    }

    String name = params.get("name");
    checkArgument(name != null, "Verification is missing a name");
    checkArgument(!name.isEmpty(), "Verification is missing a name");
    String value = params.get("value");
    checkArgument(value != null, "Verification is missing a value");
    checkArgument(!value.isEmpty(), "Verification is missing a value");
    String abstain = params.get("abstain");
    String rerun = params.get("rerun");
    VerificationInfo data = new VerificationInfo();
    data.name = name;
    data.value = Short.parseShort(value);
    data.abstain = Boolean.valueOf(abstain);
    data.rerun = Boolean.valueOf(rerun);
    data.url = params.get("url");
    data.reporter = params.get("reporter");
    data.comment = params.get("comment");
    data.category = params.get("category");
    data.duration = params.get("duration");
    jobResult.put(name, data);
  }

  @Inject
  private PostVerification postVerification;

  @Inject
  private PatchSetParser psParser;

  @Inject
  private Revisions revisions;

  @Inject
  private ChangesCollection changes;

  private Map<String, VerificationInfo> jobResult = Maps.newHashMap();

  @Override
  protected void run() throws AuthException, PermissionBackendException, UnloggedFailure {
    boolean ok = true;

    permissionBackend.user(userProvider).check(
      new PluginPermission(pluginName, SaveReportCapability.ID));

    for (PatchSet patchSet : patchSets) {
      try {
        verifyOne(patchSet);
      } catch (UnloggedFailure e) {
        ok = false;
        writeError("error: " + e.getMessage() + "\n");
      }
    }

    if (!ok) {
      throw new UnloggedFailure(1, "one or more verifications failed;"
          + " review output above");
    }
  }

  private void applyVerification(PatchSet patchSet, VerifyInput verify)
      throws RestApiException, OrmException, IOException, PermissionBackendException {
    RevisionResource revResource = revisions.parse(
        changes.parse(patchSet.getId().getParentKey()),
        IdString.fromUrl(patchSet.getId().getId()));
    postVerification.apply(revResource, verify);
  }

  private void verifyOne(PatchSet patchSet) throws PermissionBackendException, UnloggedFailure {
    VerifyInput verify = new VerifyInput();
    verify.verifications = jobResult;
    try {
      applyVerification(patchSet, verify);
    } catch (RestApiException | OrmException
        | IOException e) {
      throw PatchSetParser.error(e.getMessage());
    }
  }

  private void writeError(String msg) {
    try {
      err.write(msg.getBytes(ENC));
    } catch (IOException e) {
    }
  }
}
