package com.googlesource.gerrit.plugins.verifystatus.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwtjsonrpc.client.impl.ser.JavaSqlTimestamp_JsonSerializer;

import java.sql.Timestamp;

public class VerificationInfo extends JavaScriptObject {
  public static VerificationInfo create() {
    return createObject().cast();
  }
  public final native String label() /*-{ return this.label; }-*/;
  public final native String url() /*-{ return this.url; }-*/;
  public final native String comment() /*-{ return this.comment; }-*/;
  public final native short value() /*-{ return this.value; }-*/;

  public final Timestamp granted() {
    Timestamp r = grantedTimestamp();
    if (r == null) {
      String s = grantedRaw();
      if (s != null) {
        r = JavaSqlTimestamp_JsonSerializer.parseTimestamp(s);
        grantedTimestamp(r);
      }
    }
    return r;
  }
  public final native String grantedRaw() /*-{ return this.granted; }-*/;
  private final native Timestamp grantedTimestamp() /*-{ return this._ts }-*/;
  private final native void grantedTimestamp(Timestamp t) /*-{ this._ts = t }-*/;

  protected VerificationInfo() {
  }
}
