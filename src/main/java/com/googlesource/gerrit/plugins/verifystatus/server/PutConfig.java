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

package com.googlesource.gerrit.plugins.verifystatus.server;

import com.google.gerrit.common.data.GlobalCapability;
import com.google.gerrit.extensions.annotations.CapabilityScope;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;
import com.google.gerrit.server.config.ConfigResource;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;

import com.googlesource.gerrit.plugins.verifystatus.server.PutConfig.Input;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;

import java.io.IOException;

@RequiresCapability(
    value = GlobalCapability.ADMINISTRATE_SERVER, scope = CapabilityScope.CORE)
public class PutConfig implements RestModifyView<ConfigResource, Input>{
  public static class Input {
    public Boolean showJobsPanel;
    public Boolean showJobsDropDownPanel;
  }

  private final PluginConfigFactory cfgFactory;
  private final SitePaths sitePaths;
  private final String pluginName;

  @Inject
  PutConfig(PluginConfigFactory cfgFactory,
      SitePaths sitePaths,
      @PluginName String pluginName) {
    this.cfgFactory = cfgFactory;
    this.sitePaths = sitePaths;
    this.pluginName = pluginName;
  }

  @Override
  public Response<?> apply(ConfigResource rsrc, Input input)
      throws IOException, ConfigInvalidException, UnprocessableEntityException {
    if (input == null) {
      input = new Input();
    }
    FileBasedConfig cfg =
        new FileBasedConfig(sitePaths.gerrit_config.toFile(), FS.DETECTED);
    cfg.load();

    if (input.showJobsPanel != null) {
      cfg.setBoolean("plugin", pluginName, "showJobsPanel",
          input.showJobsPanel);
    } else {
      cfg.unset("plugin", pluginName, "showJobsPanel");
    }
    if (input.showJobsDropDownPanel != null) {
      cfg.setBoolean("plugin", pluginName, "showJobsDropDownPanel",
          input.showJobsDropDownPanel);
    } else {
      cfg.unset("plugin", pluginName, "showJobsDropDownPanel");
    }

    cfg.save();
    cfgFactory.getFromGerritConfig(pluginName, true);
    return Response.none();
  }
}
