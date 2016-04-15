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

package com.googlesource.gerrit.plugins.verifystatus.client;

import com.google.gerrit.client.GerritUiExtensionPoint;
import com.google.gerrit.client.info.ChangeInfo;
import com.google.gerrit.client.info.ChangeInfo.RevisionInfo;
import com.google.gerrit.client.rpc.NativeMap;
import com.google.gerrit.plugin.client.FormatUtil;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.extension.Panel;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;

import java.util.Map;
import java.util.TreeMap;

/**
 * Extension for change screen that displays a status in the header bar.
 */
public class JobsDropDownPanel extends FlowPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      panel.setWidget(new JobsDropDownPanel(panel));
    }
  }

  JobsDropDownPanel(Panel panel) {
    ChangeInfo change =
        panel.getObject(GerritUiExtensionPoint.Key.CHANGE_INFO).cast();
    RevisionInfo rev =
        panel.getObject(GerritUiExtensionPoint.Key.REVISION_INFO).cast();
    new RestApi("changes").id(change.id()).view("revisions").id(rev.id())
        .view(Plugin.get().getPluginName(), "verifications")
        .get(new AsyncCallback<NativeMap<VerificationInfo>>() {
          @Override
          public void onSuccess(NativeMap<VerificationInfo> result) {
            if (!result.isEmpty()) {
              Map<String, VerificationInfo> jobs = new TreeMap<>();
              for (String key : result.keySet()) {
                jobs.put(key, result.get(key));
              }
              display(jobs);
            }
          }

          @Override
          public void onFailure(Throwable caught) {
            // never invoked
          }
        });
  }

  private void display(Map<String, VerificationInfo> jobs) {
    int row = 0;
    int column = 5;
    Grid grid = new Grid(row, column);
    for (Map.Entry<String, VerificationInfo> job : jobs.entrySet()) {
      grid.insertRow(row);
      HorizontalPanel p = new HorizontalPanel();
      short vote = job.getValue().value();
      if (vote > 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.greenCheck()));
      } else if (vote < 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.redNot()));
      } else if (vote == 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.warning()));
      }
      p.add(new InlineHyperlink(job.getKey(), job.getValue().url()));
      p.add(new InlineLabel("(" + job.getValue().duration() + ")"));
      if (job.getValue().abstain()) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.info()));
      }
      grid.setWidget(row, 1, p);
      grid.setWidget(row, 2, new InlineLabel(job.getValue().category()));
      grid.setWidget(row, 3, new InlineLabel(job.getValue().reporter()));
      grid.setWidget(row, 4,
          new InlineLabel(FormatUtil.shortFormat(job.getValue().granted())));
      row++;
    }
    add(new PopDownButton("Jobs", grid));
  }
}
