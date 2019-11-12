// Copyright (C) 2019 The Android Open Source Project
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

import com.google.gwtorm.client.Column;
import com.google.gwtorm.client.IntKey;

public final class DbPatchSetId extends IntKey<DbChangeId> {
  private static final long serialVersionUID = 1L;

  @Column(id = 1)
  public DbChangeId changeId;

  @Column(id = 2)
  public int patchSetId;

  public DbPatchSetId() {
    changeId = new DbChangeId();
  }

  public DbPatchSetId(DbChangeId changeId, int id) {
    this.changeId = changeId;
    this.patchSetId = id;
  }

  @Override
  public DbChangeId getParentKey() {
    return changeId;
  }

  @Override
  public int get() {
    return patchSetId;
  }

  @Override
  protected void set(int newValue) {
    patchSetId = newValue;
  }
}
