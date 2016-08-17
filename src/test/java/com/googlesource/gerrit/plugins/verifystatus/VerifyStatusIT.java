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

package com.googlesource.gerrit.plugins.verifystatus;

import static com.google.common.truth.Truth.assertThat;

import com.google.gerrit.acceptance.PluginDaemonTest;
import com.google.gerrit.acceptance.RestResponse;
import com.google.gwtorm.server.OrmException;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.junit.Test;

import java.io.IOException;

public class VerifyStatusIT extends PluginDaemonTest {

  @Override
  protected void beforeTestServerStarts() throws IOException,
      ConfigInvalidException, OrmException {
    setPluginConfigString("dbType", "h2");
    setPluginConfigString("database", testSite + "/db/" + "TestCiDB");

    // TODO: initialize the TestCiDB
    // basically execute com.googlesource.gerrit.plugins.verifystatus.init.run()
  }


  @Test
  public void noVerificationsTest() throws Exception {
    createChange();
    RestResponse response =
        adminRestSession.get("/changes/1/revisions/1/verify-status~verifications");
    assertThat(response.getEntityContent())
        .contains("verifications");
  }
}
