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

package com.googlesource.gerrit.plugins.verifystatus.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gerrit.common.Nullable;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.change.RevisionResource;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.ResultSet;
import com.google.gwtorm.server.SchemaFactory;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.verifystatus.common.VerificationInfo;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.kohsuke.args4j.Option;

public class GetVerifications implements RestReadView<RevisionResource> {
  private final SchemaFactory<CiDb> schemaFactory;

  @Inject
  GetVerifications(SchemaFactory<CiDb> schemaFactory) {
    this.schemaFactory = schemaFactory;
  }

  @Option(
      name = "--sort",
      aliases = {"-s"},
      metaVar = "SORT",
      usage = "Sort the list by an entry")
  private JobsSorting sort;

  private String filter;

  @Option(
      name = "--filter",
      aliases = {"-f"},
      metaVar = "FILTER",
      usage = "filter the results")
  public void setFilter(String filter) {
    this.filter = filter.toUpperCase();
  }

  private VerificationInfo createVerificationInfo(PatchSetVerification v) {
    VerificationInfo info = new VerificationInfo();
    info.value = v.getValue();
    info.abstain = v.getAbstain();
    info.rerun = v.getRerun();
    info.url = v.getUrl();
    info.name = v.getName();
    info.reporter = v.getReporter();
    info.comment = v.getComment();
    info.granted = v.getGranted();
    info.category = v.getCategory();
    info.duration = v.getDuration();
    return info;
  }

  private void sortJobs(List<PatchSetVerification> jobs, @Nullable JobsSorting order) {
    if (order == null) {
      return;
    }
    switch (order) {
      case REPORTER:
        // sort the jobs list by reporter(A-Z)/Name(A-Z)/Granted(Z-A)
        Collections.sort(
            jobs,
            new Comparator<PatchSetVerification>() {
              @Override
              public int compare(PatchSetVerification a, PatchSetVerification b) {
                return new CompareToBuilder()
                    .append(a.getReporter(), b.getReporter())
                    .append(a.getName(), b.getName())
                    .append(b.getGranted(), a.getGranted())
                    .toComparison();
              }
            });
        break;
      case NAME:
        // sort the jobs list by Name(A-Z)/Granted(Z-A)
        Collections.sort(
            jobs,
            new Comparator<PatchSetVerification>() {
              @Override
              public int compare(PatchSetVerification a, PatchSetVerification b) {
                return new CompareToBuilder()
                    .append(a.getName(), b.getName())
                    .append(b.getGranted(), a.getGranted())
                    .toComparison();
              }
            });
        break;
      case DATE:
        // sort the jobs list by Granted(Z-A)
        Collections.sort(
            jobs,
            new Comparator<PatchSetVerification>() {
              @Override
              public int compare(PatchSetVerification a, PatchSetVerification b) {
                return new CompareToBuilder().append(b.getGranted(), a.getGranted()).toComparison();
              }
            });
        break;
      default:
        break;
    }
  }

  @Override
  public Response<Map<String, VerificationInfo>> apply(RevisionResource rsrc)
      throws IOException, OrmException {
    Map<String, VerificationInfo> out = Maps.newLinkedHashMap();
    try (CiDb db = schemaFactory.open()) {
      DbPatchSetId id =
          new DbPatchSetId(
              new DbChangeId(rsrc.getPatchSet().id().changeId().get()),
              rsrc.getPatchSet().id().get());

      ResultSet<PatchSetVerification> rs = db.patchSetVerifications().byPatchSet(id);
      List<PatchSetVerification> result = rs.toList();
      List<PatchSetVerification> jobs = Lists.newLinkedList();

      // filter jobs
      boolean isSorted = false;
      if (filter != null && !filter.isEmpty()) {
        if (filter.equals("CURRENT")) {
          // logic to get current jobs assumes list is sorted by reporter
          sortJobs(result, JobsSorting.REPORTER);
          isSorted = true;
          String prevReporter = "";
          String prevName = "";
          for (PatchSetVerification v : result) {
            String reporter = v.getReporter();
            if (reporter == null) {
              reporter = "(unknown)";
            }
            String jobName = v.getName();
            if (!reporter.equals(prevReporter)) {
              jobs.add(v);
            } else if (!jobName.equals(prevName)) {
              jobs.add(v);
            }
            prevReporter = reporter;
            prevName = jobName;
          }
        } else if (filter.equals("FAILED")) {
          for (PatchSetVerification v : result) {
            if (v.getValue() < 0) {
              jobs.add(v);
            }
          }
        } else {
          // assume no filtering for an invalid filter option
          jobs.addAll(result);
        }
      } else {
        jobs.addAll(result);
      }

      // sort jobs
      if ((sort != JobsSorting.REPORTER) || !isSorted) {
        sortJobs(jobs, sort);
      }

      for (PatchSetVerification v : jobs) {
        out.put(v.getJobId().get(), createVerificationInfo(v));
      }
    }
    return Response.ok(out);
  }
}
