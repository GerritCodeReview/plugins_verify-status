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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Extension for change screen that displays a status below the label info.
 */
public class JobsPanel extends FlowPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      panel.setWidget(new JobsPanel(panel));
    }
  }

  JobsPanel(Panel panel) {
    ChangeInfo change =
        panel.getObject(GerritUiExtensionPoint.Key.CHANGE_INFO).cast();
    RevisionInfo rev =
        panel.getObject(GerritUiExtensionPoint.Key.REVISION_INFO).cast();
    new RestApi("changes").id(change.id()).view("revisions").id(rev.id())
        .view(Plugin.get().getPluginName(), "verifications")
        .addParameter("sort", "REPORTER")
        .addParameter("filter", "CURRENT")
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
    int row = 0;
    int column = 1;
    Grid grid = new Grid(row, column);
    for (String key : jobs.keySet()) {
      grid.insertRow(row);
      HorizontalPanel p = new HorizontalPanel();
      short vote = jobs.get(key).value();
      if (vote > 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.greenCheck()));
      } else if (vote < 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.redNot()));
      } else if (vote == 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.warning()));
      }
      InlineHyperlink link = new InlineHyperlink(jobs.get(key).name(), jobs.get(key).url());
      link.setTitle("view logs");
      p.add(link);
      InlineLabel label = new InlineLabel(" (" + jobs.get(key).duration() + ")");
      label.setTitle("duration");
      p.add(label);
      if (jobs.get(key).category() == "recheck") {
        Image img = new Image(VerifyStatusPlugin.RESOURCES.rerun());
        img.setTitle("recheck");
        p.add(img);
      }
      if (jobs.get(key).abstain()) {
        Image img = new Image(VerifyStatusPlugin.RESOURCES.info());
        img.setTitle("non voting");
        p.add(img);
      }
      grid.setWidget(row, 0, p);
      row++;
    }
    add(grid);
  }
}
