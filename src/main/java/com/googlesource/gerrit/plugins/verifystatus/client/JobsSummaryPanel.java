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

import com.google.gerrit.client.GerritUiExtensionPoint;
import com.google.gerrit.client.info.ChangeInfo;
import com.google.gerrit.client.info.ChangeInfo.RevisionInfo;
import com.google.gerrit.client.rpc.NativeMap;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.extension.Panel;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.Label;

/** Extension for change screen that displays job summary table. */
public class JobsSummaryPanel extends FlowPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      RevisionInfo rev = panel.getObject(GerritUiExtensionPoint.Key.REVISION_INFO).cast();
      if (rev.isEdit()) {
        return;
      }

      panel.setWidget(new JobsSummaryPanel(panel));
    }
  }

  private static final String COLOR_GREEN = "#060";
  private static final String COLOR_RED = "#F00";

  JobsSummaryPanel(Panel panel) {
    final ChangeInfo change = panel.getObject(GerritUiExtensionPoint.Key.CHANGE_INFO).cast();
    String decodedChangeId = URL.decodePathSegment(change.id());
    final RevisionInfo rev = panel.getObject(GerritUiExtensionPoint.Key.REVISION_INFO).cast();
    new RestApi("changes")
        .id(decodedChangeId)
        .view("revisions")
        .id(rev.id())
        .view(Plugin.get().getPluginName(), "verifications")
        .addParameter("sort", "REPORTER")
        .addParameter("filter", "CURRENT")
        .get(
            new AsyncCallback<NativeMap<VerificationInfo>>() {
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
    Grid g = createGrid(2, 3);
    g.setText(0, 0, "Passed");
    g.setText(0, 1, "Failed");
    g.setText(0, 2, "Unstable");

    int pass = 0;
    int fail = 0;
    int unstable = 0;
    for (String key : jobs.keySet()) {
      int value = jobs.get(key).value();
      if (value > 0) {
        pass++;
      } else if (value < 0) {
        fail++;
      } else {
        unstable++;
      }
    }

    Label passedLbl = new Label(Integer.toString(pass));
    passedLbl.getElement().getStyle().setColor(COLOR_GREEN);
    g.setWidget(1, 0, passedLbl);
    Label failedLbl = new Label(Integer.toString(fail));
    failedLbl.getElement().getStyle().setColor(COLOR_RED);
    g.setWidget(1, 1, failedLbl);
    Label unstableLbl = new Label(Integer.toString(unstable));
    g.setWidget(1, 2, unstableLbl);
    add(g);
  }

  private static Grid createGrid(int rows, int columns) {
    Grid g = new Grid(rows, columns);
    g.addStyleName("infoBlock");
    g.addStyleName("changeTable");

    CellFormatter fmt = g.getCellFormatter();

    for (int c = 0; c < columns; c++) {
      fmt.addStyleName(0, c, "header");
      fmt.addStyleName(0, c, "topmost");
    }

    for (int r = 1; r < rows; r++) {
      fmt.addStyleName(r, 0, "leftMostCell");

      for (int c = 1; c < columns; c++) {
        fmt.addStyleName(r, c, "dataCell");
      }
    }

    for (int c = 0; c < columns; c++) {
      fmt.addStyleName(rows - 1, c, "bottomheader");
    }

    return g;
  }
}
