package com.googlesource.gerrit.plugins.verifystatus.client;

import com.google.gwt.core.client.JavaScriptObject;

public class VerificationInfo extends JavaScriptObject {
  public static VerificationInfo create() {
    return createObject().cast();
  }
  public final native String label() /*-{ return this.label; }-*/;
  public final native String url() /*-{ return this.url; }-*/;
  public final native String comment() /*-{ return this.comment; }-*/;
  public final native String granted() /*-{ return this.granted; }-*/;
  public final native short value() /*-{ return this.value; }-*/;

  protected VerificationInfo() {
  }
}
