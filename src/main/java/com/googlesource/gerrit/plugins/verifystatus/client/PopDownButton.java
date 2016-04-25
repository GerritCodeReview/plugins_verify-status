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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtexpui.safehtml.client.SafeHtmlBuilder;

/**
 * Pop down button in change screen.
 *
 * This class implements a button that on click opens a pop down panel with the
 * provided widget, similar to the "Patch Sets", "Download" or "Included In" pop
 * down panels on the change screen.
 *
 * This class can *only* be used within a panel that extends the header line of
 * the change screen, but will not work standalone.
 */
abstract public class PopDownButton extends Button {
  protected final Widget widget;
  protected PopupPanel popup;

  public PopDownButton(String text, Widget widget) {
    // Create Button with inner div. This is required to get proper styling
    // in the context of the change screen.
    super((new SafeHtmlBuilder()).openDiv().append(text).closeDiv());
    getElement().removeClassName("gwt-Button");
    addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        show();
      }
    });
    this.widget = widget;
  }

  abstract protected void show();
}
