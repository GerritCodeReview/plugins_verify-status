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
import com.google.gerrit.client.rpc.Natives;
import com.google.gerrit.plugin.client.FormatUtil;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.extension.Panel;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;

import java.util.List;

/**
 * Extension for change screen that displays a status in the header bar.
 */
public class BuildsDropDownPanel extends FlowPanel {
  static class Factory implements Panel.EntryPoint {
    @Override
    public void onLoad(Panel panel) {
      panel.setWidget(new BuildsDropDownPanel(panel));
    }
  }

  BuildsDropDownPanel(Panel panel) {
    ChangeInfo change =
        panel.getObject(GerritUiExtensionPoint.Key.CHANGE_INFO).cast();
    RevisionInfo rev =
        panel.getObject(GerritUiExtensionPoint.Key.REVISION_INFO).cast();
    new RestApi("changes")
      .id(change.id())
      .view("revisions")
      .id(rev.id())
      .view(Plugin.get().getPluginName(), "verifications")
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

  private void display(NativeMap<VerificationInfo> vmap) {
    List<VerificationInfo> list = Natives.asList(vmap.values());
    Grid g = new Grid(1, 4);
    g.addStyleName("verificationInfo");
    CellFormatter fmt = g.getCellFormatter();

    // table header
    g.setText(0, 0, "Job");
    fmt.addStyleName(0, 0, "header");
    fmt.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
    g.setText(0, 1, "Date");
    fmt.addStyleName(0, 1, "header");
    fmt.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
    g.setText(0, 2, "Result");
    fmt.addStyleName(0, 2, "header");
    fmt.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);

    // add job results to table
    int i=1;
    for (VerificationInfo v : list) {
      g.insertRow(i);
      g.setWidget(i, 0, new InlineLabel(v.label()));
      g.setWidget(i, 1, new InlineLabel(FormatUtil.shortFormat(v.granted())));
      HorizontalPanel p = new HorizontalPanel();
      short vote = v.value();
      if (vote > 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.greenCheck()));
      } else if (vote < 0) {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.redNot()));
      } else {
        p.add(new Image(VerifyStatusPlugin.RESOURCES.info()));
      }
      p.add(new InlineHyperlink(v.comment(), v.url()));
      g.setWidget(i, 2, p);
      i++;
     }
    add(new PopDownButton("Builds", g));
  }
}
