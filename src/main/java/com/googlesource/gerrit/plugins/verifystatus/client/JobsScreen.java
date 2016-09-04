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

package com.googlesource.gerrit.plugins.verifystatus.client;

import com.google.gerrit.client.rpc.NativeMap;
import com.google.gerrit.plugin.client.FormatUtil;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gerrit.plugin.client.screen.Screen;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class JobsScreen extends VerticalPanel {
  static class Factory implements Screen.EntryPoint {
    @Override
    public void onLoad(Screen screen) {
      screen.setPageTitle("Reports for patchset " + screen.getToken(1));
      screen.show(new JobsScreen(screen.getToken(1)));
    }
  }

  JobsScreen(final String patchsetId) {
    setStyleName("verifystatus-panel");
    String[] id = patchsetId.split(",");
    String decodedChagneId = URL.decodePathSegment(id[0]);
    String revId = id[1];

    new RestApi("changes").id(decodedChagneId).view("revisions").id(revId)
        .view(Plugin.get().getPluginName(), "verifications")
        .addParameter("sort", "REPORTER")
        .get(new AsyncCallback<NativeMap<VerificationInfo>>() {
          @Override
          public void onSuccess(NativeMap<VerificationInfo> result) {
            if (!result.isEmpty()) {
              display(result);
            }
          }

          @Override
          public void onFailure(Throwable caught) {
            // never invoked
          }
        });
  }

  private void display(NativeMap<VerificationInfo> jobs) {
    int columns = 7;
    FlexTable t = new FlexTable();
    t.setStyleName("verifystatus-jobsTable");
    FlexCellFormatter fmt = t.getFlexCellFormatter();
    for (int c = 0; c < columns; c++) {
      fmt.addStyleName(0, c, "dataHeader");
      fmt.addStyleName(0, c, "topMostCell");
    }
    fmt.addStyleName(0, 0, "leftMostCell");

    t.setText(0, 0, "Result");
    t.setText(0, 1, "Name");
    t.setText(0, 2, "Duration");
    t.setText(0, 3, "Voting");
    t.setText(0, 4, "Rerun");
    t.setText(0, 5, "Category");
    t.setText(0, 6, "Reporter");
    t.setText(0, 7, "Date");

    int row = 1;
    for (String key : jobs.keySet()) {
      VerificationInfo vi = jobs.get(key);

      for (int c = 0; c < columns; c++) {
        fmt.addStyleName(row, c, "dataCell");
        fmt.addStyleName(row, 0, "leftMostCell");
      }
      short vote = vi.value();
      if (vote > 0) {
        t.setWidget(row, 0,
            new Image(VerifyStatusPlugin.RESOURCES.greenCheck()));
      } else if (vote < 0) {
        t.setWidget(row, 0, new Image(VerifyStatusPlugin.RESOURCES.redNot()));
      } else if (vote == 0) {
        t.setWidget(row, 0, new Image(VerifyStatusPlugin.RESOURCES.warning()));
      }
      Anchor anchor = new Anchor(vi.name(), vi.url());
      t.setWidget(row, 1, anchor);
      t.setText(row, 2, vi.duration());
      if (vi.abstain()) {
        t.setText(row, 3, "non-voting");
      } else {
        t.setText(row, 3, "voting");
      }
      if (vi.rerun()) {
        t.setText(row, 4, "Y");
      } else {
        t.setText(row, 4, "N");
      }

      t.setText(row, 5, vi.category());
      t.setText(row, 6, vi.reporter());
      t.setText(row, 7, FormatUtil.shortFormat(vi.granted()));
      row++;
    }
    add(t);
  }
}
