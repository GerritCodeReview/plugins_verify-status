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

package com.googlesource.gerrit.plugins.verifystatus.server.schema;

import com.google.common.base.Strings;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;

public class CiDataSourceTypeGuesser {

  private final PluginConfig config;

  @Inject
  CiDataSourceTypeGuesser(SitePaths site, @PluginName String pluginName) {
    File file = site.gerrit_config.toFile();
    FileBasedConfig cfg = new FileBasedConfig(file, FS.DETECTED);
    try {
      cfg.load();
    } catch (IOException | ConfigInvalidException e) {
      throw new ProvisionException(e.getMessage(), e);
    }
    this.config = PluginConfig.create(pluginName, cfg, null);
  }

  public String guessDataSourceType() {
    String dbType = config.getString("dbType");
    if (Strings.isNullOrEmpty(dbType)) {
      throw new ProvisionException(String.format("'dbType' must be defined in config file"));
    }
    return dbType.toLowerCase();
  }
}
