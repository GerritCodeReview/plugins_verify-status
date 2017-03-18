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

public class ConfigInfo extends JavaScriptObject {
  final native boolean showJobsSummaryPanel() /*-{ return this.show_jobs_summary_panel ? true : false; }-*/;
  final native boolean showJobsPanel() /*-{ return this.show_jobs_panel ? true : false; }-*/;
  final native boolean showJobsDropDownPanel() /*-{ return this.show_jobs_drop_down_panel ? true : false; }-*/;
  final native String sortJobsPanel() /*-{ return this.sort_jobs_panel }-*/;
  final native String sortJobsDropDownPanel() /*-{ return this.sort_jobs_drop_down_panel }-*/;

  final native void setShowJobsSummaryPanel(boolean s) /*-{ this.show_jobs_summary_panel = s; }-*/;
  final native void setShowJobsPanel(boolean s) /*-{ this.show_jobs_panel = s; }-*/;
  final native void setShowJobsDropDownPanel(boolean s) /*-{ this.show_jobs_drop_down_panel = s; }-*/;
  final native void setSortJobsPanel(String s) /*-{ this.sort_jobs_panel = s; }-*/;
  final native void setSortJobsDropDownPanel(String s) /*-{ this.sort_jobs_drop_down_panel = s; }-*/;

  static ConfigInfo create() {
    ConfigInfo g = (ConfigInfo) createObject();
    return g;
  }

  protected ConfigInfo() {
  }
}
