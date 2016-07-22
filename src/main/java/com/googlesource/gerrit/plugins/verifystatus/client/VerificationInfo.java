package com.googlesource.gerrit.plugins.verifystatus.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwtjsonrpc.client.impl.ser.JavaSqlTimestamp_JsonSerializer;

import java.sql.Timestamp;

public class VerificationInfo extends JavaScriptObject {

  public final native String url() /*-{ return this.url; }-*/;
  public final native String name() /*-{ return this.name; }-*/;
  public final native String comment() /*-{ return this.comment; }-*/;
  public final native short value() /*-{ return this.value; }-*/;
  public final native boolean abstain() /*-{ return this.abstain || false; }-*/;
  public final native String category() /*-{ return this.category; }-*/;
  public final native String duration() /*-{ return this.duration; }-*/;
  public final native String reporter() /*-{ return this.reporter; }-*/;

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
  private final native String grantedRaw() /*-{ return this.granted; }-*/;
  private final native Timestamp grantedTimestamp() /*-{ return this._ts }-*/;
  private final native void grantedTimestamp(Timestamp t) /*-{ this._ts = t }-*/;

  protected VerificationInfo() {
  }
}
