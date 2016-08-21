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

package  com.googlesource.gerrit.plugins.verifystatus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gerrit.common.TimeUtil;
import com.google.gerrit.common.errors.PermissionDeniedException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.BadRequestException;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;
import com.google.gerrit.reviewdb.client.LabelId;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.CapabilityControl;
import com.google.gerrit.server.change.RevisionResource;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.SchemaFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import com.googlesource.gerrit.plugins.verifystatus.common.VerificationInfo;
import com.googlesource.gerrit.plugins.verifystatus.common.VerifyInput;
import com.googlesource.gerrit.plugins.verifystatus.server.CiDb;
import com.googlesource.gerrit.plugins.verifystatus.server.PatchSetVerification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
@RequiresCapability(SaveReportCapability.ID)
public class PostVerification
    implements RestModifyView<RevisionResource, VerifyInput> {
  private static final Logger log =
      LoggerFactory.getLogger(PostVerification.class);
  private final SchemaFactory<CiDb> schemaFactory;
  private final String pluginName;
  private final Provider<CurrentUser> userProvider;

  @Inject
  PostVerification(@PluginName String pluginName,
      Provider<CurrentUser> userProvider,
      SchemaFactory<CiDb> schemaFactory) {
    this.pluginName = pluginName;
    this.userProvider = userProvider;
    this.schemaFactory = schemaFactory;
  }

  @Override
  public Response<?> apply(RevisionResource revision, VerifyInput input)
      throws AuthException, BadRequestException, UnprocessableEntityException,
      OrmException, IOException {

    try {
      checkPermission();
    } catch (PermissionDeniedException err) {
      throw new BadRequestException("fatal: " + err.getMessage());
    }

    if (input.verifications == null) {
      throw new BadRequestException("Missing verifications field");
    }

    try (CiDb db = schemaFactory.open()) {
      // Only needed for Google Megastore (that we don't use yet)
      db.patchSetVerifications().beginTransaction(null);
      boolean dirty = false;
      try {
        dirty |= updateLabels(revision, db, input.verifications);
        if (dirty) {
          db.commit();
        }
      } finally {
        db.rollback();
      }
    }
    return Response.none();
  }

  private boolean updateLabels(RevisionResource resource, CiDb db,
      Map<String, VerificationInfo> jobs)
      throws OrmException, BadRequestException {
    Preconditions.checkNotNull(jobs);

    List<PatchSetVerification> ups = Lists.newArrayList();
    Map<String, PatchSetVerification> current = scanLabels(resource, db);

    Timestamp ts = TimeUtil.nowTs();
    for (Map.Entry<String, VerificationInfo> ent : jobs.entrySet()) {
      String name = ent.getKey();
      if (name == null) {
        throw new BadRequestException("Missing name field");
      }
      PatchSetVerification c = current.remove(name);
      Short value = ent.getValue().value;
      if (value == null) {
        throw new BadRequestException("Missing value field");
      }
      if (c != null) {
        // update a result
        c.setGranted(ts);
        c.setValue(value);
        if (Boolean.TRUE.equals(ent.getValue().abstain)) {
          c.setAbstain(true);
        }
        String url = ent.getValue().url;
        if (url != null) {
          c.setUrl(url);
        }
        String job_name = c.getName();
        if (job_name != null) {
          c.setName(job_name);
        }
        String reporter = ent.getValue().reporter;
        if (reporter != null) {
          c.setReporter(reporter);
        }
        String comment = ent.getValue().comment;
        if (comment != null) {
          c.setComment(comment);
        }
        String category = ent.getValue().category;
        if (category != null) {
          c.setCategory(category);
        }
        String duration = ent.getValue().duration;
        if (duration != null) {
          c.setDuration(duration);
        }
        log.info("Updating job " + c.getJob() + " for change "
            + c.getPatchSetId());
        ups.add(c);
      } else {
        // add new result
        String job_id = UUID.randomUUID().toString();
        c = new PatchSetVerification(new PatchSetVerification.Key(
                resource.getPatchSet().getId(),
                new LabelId(job_id)),
            value, ts);
        c.setAbstain(ent.getValue().abstain);
        c.setUrl(ent.getValue().url);
        c.setName(name);
        c.setReporter(ent.getValue().reporter);
        c.setComment(ent.getValue().comment);
        c.setCategory(ent.getValue().category);
        c.setDuration(ent.getValue().duration);
        log.info("Adding job " + c.getJob() + " for change "
            + c.getPatchSetId());
        ups.add(c);
      }
    }

    db.patchSetVerifications().upsert(ups);
    return !ups.isEmpty();
  }

  private Map<String, PatchSetVerification> scanLabels(
      RevisionResource resource, CiDb db)
      throws OrmException {
    Map<String, PatchSetVerification> current = Maps.newHashMap();
    for (PatchSetVerification v : db.patchSetVerifications()
        .byPatchSet(resource.getPatchSet().getId())) {
      current.put(v.getJobId().get(), v);
    }
    return current;
  }

  /**
   * Assert that the current user is permitted to perform saving of verification
   * reports.
   * <p>
   * As the @RequireCapability guards at various entry points of internal
   * commands implicitly add administrators (which we want to avoid), we also
   * check permissions within QueryShell and grant access only to those who
   * canPerformRawQuery, regardless of whether they are administrators or not.
   *
   * @throws PermissionDeniedException
   */
  private void checkPermission() throws PermissionDeniedException {
    CapabilityControl ctl = userProvider.get().getCapabilities();
    if (!ctl.canPerform(SaveReportCapability.getName(pluginName))) {
      throw new PermissionDeniedException(String.format(
          "%s does not have \"%s\" capability.",
          userProvider.get().getUserName(),
          new SaveReportCapability().getDescription()));
    }
  }
}
