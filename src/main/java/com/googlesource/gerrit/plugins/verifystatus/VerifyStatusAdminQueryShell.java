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

import com.google.gerrit.common.errors.PermissionDeniedException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.extensions.api.access.PluginPermission;
import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.gerrit.sshd.AdminHighPriorityCommand;
import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.kohsuke.args4j.Option;

/** Opens a query processor. */
@AdminHighPriorityCommand
@RequiresCapability(AccessCiDatabaseCapability.ID)
@CommandMetaData(name = "gsql", description = "Administrative interface to CI database")
public class VerifyStatusAdminQueryShell extends SshCommand {
  private final String pluginName;
  private final Provider<CurrentUser> userProvider;
  private final PermissionBackend permissionBackend;

  @Inject
  private VerifyStatusQueryShell.Factory factory;

  @Option(name = "--format", usage = "Set output format")
  private VerifyStatusQueryShell.OutputFormat format = VerifyStatusQueryShell.OutputFormat.PRETTY;

  @Option(name = "-c", metaVar = "SQL QUERY", usage = "Query to execute")
  private String query;

  @Inject
  VerifyStatusAdminQueryShell(@PluginName String pluginName,
      Provider<CurrentUser> userProvider,
      PermissionBackend permissionBackend) {
    this.pluginName = pluginName;
    this.userProvider = userProvider;
    this.permissionBackend = permissionBackend;
  }

  @Override
  protected void run() throws AuthException, PermissionBackendException, UnloggedFailure {
    // Explictly check that current user was granted plugin's own capability,
    // regardless of whether he is administrator or not.
    permissionBackend.user(userProvider).check(
      new PluginPermission(pluginName, AccessCiDatabaseCapability.ID));

    final VerifyStatusQueryShell shell = factory.create(in, out);
    shell.setOutputFormat(format);
    if (query != null) {
      shell.execute(query);
    } else {
      shell.run();
    }
  }
}
