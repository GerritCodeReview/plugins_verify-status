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
import static com.google.gerrit.acceptance.testsuite.project.TestProjectUpdate.allowCapability;

import com.google.common.collect.Iterables;
import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.PushOneCommit.Result;
import com.google.gerrit.acceptance.RestResponse;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.acceptance.UseLocalDisk;
import com.google.gerrit.acceptance.config.GerritConfig;
import com.google.gerrit.acceptance.testsuite.project.ProjectOperations;
import com.google.gson.reflect.TypeToken;
import com.google.gwtorm.jdbc.SimpleDataSource;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.verifystatus.common.VerificationInfo;
import com.googlesource.gerrit.plugins.verifystatus.common.VerifyInput;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;

@TestPlugin(
    name = "verify-status",
    sysModule = "com.googlesource.gerrit.plugins.verifystatus.GlobalModule",
    httpModule = "com.googlesource.gerrit.plugins.verifystatus.HttpModule",
    sshModule = "com.googlesource.gerrit.plugins.verifystatus.SshModule")
public class VerifyStatusIT extends LightweightPluginDaemonTest {
  private static final String NAME = "verify-status";
  private static final String DB_TYPE_CONFIG = "plugin." + NAME + ".dbType";
  private static final String DB_URL_CONFIG = "plugin." + NAME + ".dbUrl";
  private static final String H2 = "h2";
  private static final String URL = "jdbc:h2:mem:TestCiDB;DB_CLOSE_DELAY=-1";
  private static final String TABLE = "PATCH_SET_VERIFICATIONS";
  private static final String CREATE_TABLE =
      "CREATE       TABLE IF NOT EXISTS "
          + TABLE
          + " ("
          + "VALUE        SMALLINT DEFAULT 0 NOT NULL,"
          + "GRANTED      TIMESTAMP NOT NULL,"
          + "URL          VARCHAR(255),"
          + "REPORTER     VARCHAR(255),"
          + "COMMENT      VARCHAR(255),"
          + "CATEGORY     VARCHAR(255),"
          + "DURATION     VARCHAR(255),"
          + "ABSTAIN      CHAR(1) DEFAULT 'N' NOT NULL,"
          + "RERUN        CHAR(1) DEFAULT 'N' NOT NULL,"
          + "NAME         VARCHAR(255),"
          + "CHANGE_ID    INTEGER DEFAULT 0 NOT NULL,"
          + "PATCH_SET_ID INTEGER DEFAULT 0 NOT NULL,"
          + "JOB_ID       VARCHAR(255) DEFAULT '' NOT NULL)";
  private static final String DELETE_TABLE = "DELETE FROM " + TABLE;

  @Inject private ProjectOperations projectOperations;

  @Before
  @Override
  public void setUpTestPlugin() throws Exception {
    super.setUpTestPlugin();

    projectOperations
        .project(allProjects)
        .forUpdate()
        .add(
            allowCapability("checks-administrateCheckers")
                .group(group("Administrators").getGroupUUID()))
        .update();

    Properties p = new Properties();
    p.setProperty("driver", "org.h2.Driver");
    p.setProperty("url", URL);

    SimpleDataSource sds = new SimpleDataSource(p);
    try (Connection c = sds.getConnection();
        Statement s = c.createStatement()) {
      s.executeUpdate(CREATE_TABLE);
      // run the tests with the clean database
      s.execute(DELETE_TABLE);
    }
  }

  @Test
  @GerritConfig(name = DB_TYPE_CONFIG, value = H2)
  @GerritConfig(name = DB_URL_CONFIG, value = URL)
  @UseLocalDisk
  public void noVerificationTest() throws Exception {
    Result c = createChange();
    Map<String, VerificationInfo> infos = getVerifications(c, null);
    assertThat(infos).hasSize(0);
  }

  @Test
  @GerritConfig(name = DB_TYPE_CONFIG, value = H2)
  @GerritConfig(name = DB_URL_CONFIG, value = URL)
  @UseLocalDisk
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

    Map<String, VerificationInfo> infos = getVerifications(c, null);
    assertThat(infos).hasSize(1);
    assertVerification(Iterables.getOnlyElement(infos.values()), i);
  }

  @Test
  @GerritConfig(name = DB_TYPE_CONFIG, value = H2)
  @GerritConfig(name = DB_URL_CONFIG, value = URL)
  @UseLocalDisk
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

    Map<String, VerificationInfo> infos = getVerifications(c, null);
    assertThat(infos).hasSize(2);
  }

  @Test
  @GerritConfig(name = DB_TYPE_CONFIG, value = H2)
  @GerritConfig(name = DB_URL_CONFIG, value = URL)
  @UseLocalDisk
  public void verificationTestNullReporter() throws Exception {
    VerifyInput in = new VerifyInput();
    in.verifications = new HashMap<>();
    VerificationInfo i = new VerificationInfo();
    i.name = "job42";
    i.value = 1;
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

    Map<String, VerificationInfo> infos = getVerifications(c, "CURRENT");
    assertThat(infos).hasSize(1);
    assertThat(Iterables.getOnlyElement(infos.values()).reporter).isNull();
    assertVerification(Iterables.getOnlyElement(infos.values()), i);
  }

  private Map<String, VerificationInfo> getVerifications(Result c, String filter) throws Exception {
    String endPoint = url(c);
    if (filter != null) {
      endPoint += "/?filter=" + filter;
    }
    RestResponse r = adminRestSession.get(endPoint);
    r.assertOK();

    return newGson()
        .fromJson(r.getReader(), new TypeToken<Map<String, VerificationInfo>>() {}.getType());
  }

  private String url(Result c) throws Exception {
    return "/changes/"
        + c.getChangeId()
        + "/revisions/"
        + c.getPatchSetId().get()
        + "/verify-status~verifications";
  }

  private static void assertVerification(VerificationInfo r, VerificationInfo e) {
    assertThat(r.value).isEqualTo(e.value);
    assertThat(r.reporter).isEqualTo(e.reporter);
    assertThat(r.comment).isEqualTo(e.comment);
    assertThat(r.url).isEqualTo(e.url);
    assertThat(r.category).isEqualTo(e.category);
    assertThat(r.duration).isEqualTo(e.duration);
    assertThat(r.rerun).isEqualTo(e.rerun);
  }
}
