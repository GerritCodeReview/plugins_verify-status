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
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.extension.Panel;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.Label;


/**
 * Extension for change screen that displays job statistics summary.
 */
public class StatsPanel extends FlowPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      panel.setWidget(new StatsPanel(panel));
    }
  }

  private final static String COLOR_GREEN = "#060";
  private final static String COLOR_RED = "#F00";

  StatsPanel(Panel panel) {
    ChangeInfo change =
        panel.getObject(GerritUiExtensionPoint.Key.CHANGE_INFO).cast();
    RevisionInfo rev =
        panel.getObject(GerritUiExtensionPoint.Key.REVISION_INFO).cast();
    new RestApi("changes").id(change.id()).view("revisions").id(rev.id())
        .view(Plugin.get().getPluginName(), "stats")
        .addParameter("filter", "CURRENT")
        .get(new AsyncCallback<VerificationStats>() {
          @Override
          public void onSuccess(VerificationStats result) {
            if (result != null) {
              int numJobs =
                  result.passes() + result.fails() + result.unstables();
              if (numJobs != 0) {
                display(result);
              }
            }
          }

          @Override
          public void onFailure(Throwable caught) {
            // never invoked
          }
        });
  }

  private void display(VerificationStats results) {
    Grid g = createGrid(2, 3);
    g.setText(0, 0, "Passed");
    g.setText(0, 1, "Failed");
    g.setText(0, 2, "Unstable");
    Label passed = new Label(Integer.toString(results.passes()));
    passed.getElement().getStyle().setColor(COLOR_GREEN);
    g.setWidget(1, 0, passed);
    Label failed = new Label(Integer.toString(results.fails()));
    failed.getElement().getStyle().setColor(COLOR_RED);
    g.setWidget(1, 1, failed);
    Label unstable = new Label(Integer.toString(results.unstables()));
    g.setWidget(1, 2, unstable);
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
