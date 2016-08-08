// Copyright (C) 2016 The Android Open Source Project
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

import com.google.common.collect.Maps;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.change.RevisionResource;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.ResultSet;
import com.google.gwtorm.server.SchemaFactory;
import com.google.inject.Inject;

import com.googlesource.gerrit.plugins.verifystatus.common.VerificationStats;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GetStats implements RestReadView<RevisionResource> {
  private final SchemaFactory<CiDb> schemaFactory;

  @Inject
  GetStats(SchemaFactory<CiDb> schemaFactory) {
    this.schemaFactory = schemaFactory;
  }

  private String filter;

  @Option(name = "--filter", aliases = {"-f"},
      metaVar = "FILTER", usage = "filter the results")
  public void setFilter(String filter) {
    this.filter = filter.toUpperCase();
  }

  @Override
  public VerificationStats apply(RevisionResource rsrc)
      throws IOException, OrmException {
    VerificationStats out = new VerificationStats();
    try (CiDb db = schemaFactory.open()) {
      ResultSet<PatchSetVerification> rs =
          db.patchSetVerifications().byPatchSet(rsrc.getPatchSet().getId());
      List<PatchSetVerification> jobs = rs.toList();
      // sort the jobs list by reporter (ascending) then reported date (descending)
      Collections.sort(jobs, new Comparator<PatchSetVerification>() {
        @Override
        public int compare(PatchSetVerification a, PatchSetVerification b) {
          return new CompareToBuilder().append(a.getReporter(), b.getReporter())
              .append(b.getGranted(), a.getGranted()).toComparison();
        }
      });

      int pass = 0;
      int fail = 0;
      int unstable = 0;
      int voting = 0;
      if (filter != null && !filter.isEmpty()) {
        if (filter.equals("CURRENT")) {
          Map<String, Timestamp> reported = Maps.newHashMap();
          for (PatchSetVerification v : jobs) {
            if (!reported.containsKey(v.getReporter())) {
              reported.put(v.getReporter(), v.getGranted());
            }
          }
          for (PatchSetVerification v : jobs) {
            Timestamp ts = v.getGranted();
            if (reported.values().contains(ts)) {
              int value = v.getValue();
              if (value > 0) {
                pass++;
              } else if (value < 0) {
                fail++;
              } else {
                unstable++;
              }
              if (!v.getAbstain()) {
                voting++;
              }
            }
          }
        }
      } else {
        for (PatchSetVerification v : jobs) {
          int value = v.getValue();
          if (value > 0) {
            pass++;
          } else if (value < 0) {
            fail++;
          } else {
            unstable++;
          }
          if (!v.getAbstain()) {
            voting++;
          }
        }
      }
      out.passed = pass;
      out.failed = fail;
      out.unstable = unstable;
      out.voting = voting;
    }
    return out;
  }
}
