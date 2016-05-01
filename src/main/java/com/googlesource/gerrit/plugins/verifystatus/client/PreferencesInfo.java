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

import com.google.gwt.core.client.JavaScriptObject;

public class PreferencesInfo extends JavaScriptObject {
  public static enum VisibleJobs {
    ALL, NONE, FIVE, TEN
  }

  public static PreferencesInfo create() {
    return (PreferencesInfo) createObject();
  }

  public final VisibleJobs visibleJobs() {
    return VisibleJobs.valueOf(_visibleJobs());
  }

  private final native String _visibleJobs() /*-{ return this.visible_jobs; }-*/;
  public  final native void visibleJobs(String n) /*-{ if(n)this.visible_jobs=n; }-*/;

  protected PreferencesInfo() {
  }
}
