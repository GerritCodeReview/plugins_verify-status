package com.googlesource.gerrit.plugins.ci.client;

import com.google.gwt.core.client.JavaScriptObject;

public class VerificationInfo extends JavaScriptObject {

  public final native String category() /*-{ return this.category; }-*/;
  public final native String url() /*-{ return this.url; }-*/;
  public final native String granted() /*-{ return this.granted; }-*/;
  public final native int value() /*-{ return this.value; }-*/;

  protected VerificationInfo() {
  }
}
