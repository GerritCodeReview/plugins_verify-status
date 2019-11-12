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

public class DbChangeId extends IntKey<com.google.gwtorm.client.Key<?>> {
  private static final long serialVersionUID = 1L;

  @Column(id = 1)
  public int id;

  protected DbChangeId() {}

  public DbChangeId(int id) {
    this.id = id;
  }

  @Override
  public int get() {
    return id;
  }

  @Override
  protected void set(int newValue) {
    id = newValue;
  }
}
