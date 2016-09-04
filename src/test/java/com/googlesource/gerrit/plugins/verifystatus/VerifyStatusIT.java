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

package com.googlesource.gerrit.plugins.verifystatus;

import static com.google.common.truth.Truth.assertThat;
import static com.google.gerrit.server.group.SystemGroupBackend.REGISTERED_USERS;

import com.google.common.collect.Iterables;
import com.google.gerrit.acceptance.PluginDaemonTest;
import com.google.gerrit.acceptance.PushOneCommit.Result;
import com.google.gerrit.acceptance.RestResponse;
import com.google.gson.reflect.TypeToken;
import com.google.gwtorm.jdbc.SimpleDataSource;

import com.googlesource.gerrit.plugins.verifystatus.common.VerificationInfo;
import com.googlesource.gerrit.plugins.verifystatus.common.VerifyInput;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class VerifyStatusIT extends PluginDaemonTest {
  private final static String TABLE = "PATCH_SET_VERIFICATIONS";
  private static final String CREATE_TABLE =
      "CREATE       TABLE IF NOT EXISTS " + TABLE + " (" +
      "VALUE        SMALLINT DEFAULT 0 NOT NULL," +
      "GRANTED      TIMESTAMP NOT NULL," +
      "URL          VARCHAR(255)," +
      "REPORTER     VARCHAR(255)," +
      "COMMENT      VARCHAR(255)," +
      "CATEGORY     VARCHAR(255)," +
      "DURATION     VARCHAR(255)," +
      "ABSTAIN      CHAR(1) DEFAULT 'N' NOT NULL," +
      "RERUN        CHAR(1) DEFAULT 'N' NOT NULL," +
      "NAME         VARCHAR(255)," +
      "CHANGE_ID    INTEGER DEFAULT 0 NOT NULL," +
      "PATCH_SET_ID INTEGER DEFAULT 0 NOT NULL," +
      "JOB_ID       VARCHAR(255) DEFAULT '' NOT NULL)";
  private static final String DELETE_TABLE =
      "DELETE FROM " + TABLE;

  @Override
  protected void beforeTestServerStarts() throws Exception {
    String url = "jdbc:h2:mem:TestCiDB;DB_CLOSE_DELAY=-1";
    Properties p = new Properties();
    p.setProperty("driver", "org.h2.Driver");
    p.setProperty("url", url);
    setPluginConfigString("dbType", "h2");
    setPluginConfigString("dbUrl", url);

    SimpleDataSource sds = new SimpleDataSource(p);
    try (Connection c = sds.getConnection();
        Statement s = c.createStatement()) {
      s.executeUpdate(CREATE_TABLE);
      // run the tests with the clean database
      s.execute(DELETE_TABLE);
    }
  }

  @Before
  public void setUp() throws Exception {
    allowGlobalCapabilities(REGISTERED_USERS,
        SaveReportCapability.getName(pluginName));
  }

  @Test
  public void noVerificationTest() throws Exception {
    Result c = createChange();
    Map<String, VerificationInfo> infos = getVerifications(c);
    assertThat(infos).hasSize(0);
  }

  @Test
  public void verificationOneTest() throws Exception {
    VerifyInput in = new VerifyInput();
    in.verifications = new HashMap<>();
    VerificationInfo i = new VerificationInfo();
    i.name = "job42";
    i.value = 1;
    i.reporter = "zuul@openstack.org";
    i.rerun = true;
    i.comment = "Test CI";
    i.url = "url";
    i.category = "bar";
    i.duration = "1h 30min";
    in.verifications.put("foo", i);

    Result c = createChange();
    String endPoint = url(c);
    RestResponse r = adminRestSession.post(endPoint, in);
    r.assertNoContent();

    Map<String, VerificationInfo> infos = getVerifications(c);
    assertThat(infos).hasSize(1);
    assertVerification(Iterables.getOnlyElement(infos.values()), i);
  }

  @Test
  public void verificationTwoTest() throws Exception {
    VerifyInput in = new VerifyInput();
    in.verifications = new HashMap<>();
    VerificationInfo i = new VerificationInfo();
    i.name = "job43";
    i.value = 1;
    i.reporter = "zuul@openstack.org";
    i.comment = "Test CI";
    i.url = "url";
    i.category = "bar";
    i.duration = "1h 30min";
    in.verifications.put(i.name, i);

    VerificationInfo j = new VerificationInfo();
    j = new VerificationInfo();
    j.name = "job44";
    j.value = -1;
    j.reporter = "zuul@openstack.org";
    j.comment = "Test CI";
    j.url = "url";
    j.category = "bar";
    j.duration = "1h 30min";
    in.verifications.put(j.name, j);

    Result c = createChange();
    String endPoint = url(c);
    RestResponse r = adminRestSession.post(endPoint, in);
    r.assertNoContent();

    Map<String, VerificationInfo> infos = getVerifications(c);
    assertThat(infos).hasSize(2);
  }

  private Map<String, VerificationInfo> getVerifications(Result c)
      throws Exception {
    String endPoint = url(c);
    RestResponse r = adminRestSession.get(endPoint);
    r.assertOK();

    return newGson().fromJson(r.getReader(),
        new TypeToken<Map<String, VerificationInfo>>() {}.getType());
  }

  private String url(Result c) throws Exception {
    return "/changes/" +
        c.getChangeId() +
        "/revisions/" +
        c.getPatchSetId().get() +
        "/verify-status~verifications";
  }

  private static void assertVerification(VerificationInfo r,
      VerificationInfo e) {
    assertThat(r.value).isEqualTo(e.value);
    assertThat(r.reporter).isEqualTo(e.reporter);
    assertThat(r.comment).isEqualTo(e.comment);
    assertThat(r.url).isEqualTo(e.url);
    assertThat(r.category).isEqualTo(e.category);
    assertThat(r.duration).isEqualTo(e.duration);
    assertThat(r.rerun).isEqualTo(e.rerun);
  }
}
