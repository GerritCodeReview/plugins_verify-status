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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtexpui.globalkey.client.GlobalKey;

/**
 * Drop down button on header line in change screen.
 */
public class JobsPopDownPanelButton extends PopDownButton {

  public JobsPopDownPanelButton(String text, Widget widget) {
    super(text, widget);
  }

  @Override
  protected void show() {
    if (popup != null) {
      getElement().getStyle().clearFontWeight();
      popup.hide();
      return;
    }

    final Widget relativeTo = getParent();
    final PopupPanel p = new PopupPanel(true) {
      @Override
      public void setPopupPosition(int left, int top) {
        top -= Document.get().getBodyOffsetTop();

        int w = Window.getScrollLeft() + Window.getClientWidth();
        int r = relativeTo.getAbsoluteLeft() + relativeTo.getOffsetWidth();
        int right = w - r;
        Style style = getElement().getStyle();
        style.clearProperty("left");
        style.setPropertyPx("right", right);
        style.setPropertyPx("top", top);
      }
    };
    Style popupStyle = p.getElement().getStyle();
    popupStyle.setBorderWidth(0, Unit.PX);
    popupStyle.setBackgroundColor("#EEEEEE");
    p.addAutoHidePartner(getElement());
    p.addCloseHandler(new CloseHandler<PopupPanel>() {
      @Override
      public void onClose(CloseEvent<PopupPanel> event) {
        if (popup == p) {
          getElement().getStyle().clearFontWeight();
          popup = null;
        }
      }
    });
    p.add(widget);
    p.showRelativeTo(relativeTo);
    GlobalKey.dialog(p);
    getElement().getStyle().setFontWeight(FontWeight.BOLD);
    popup = p;
  }
}
