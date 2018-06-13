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

package com.googlesource.gerrit.plugins.verifystatus.server;

import com.google.gerrit.reviewdb.client.LabelId;
import com.google.gerrit.reviewdb.client.PatchSet;
import com.google.gwtorm.client.Column;
import com.google.gwtorm.client.CompoundKey;
import java.sql.Timestamp;
import java.util.Objects;

public class PatchSetVerification {

  public static class Key extends CompoundKey<PatchSet.Id> {
    private static final long serialVersionUID = 1L;

    @Column(id = 1, name = Column.NONE)
    protected PatchSet.Id patchSetId;

    @Column(id = 2)
    protected LabelId jobId;

    protected Key() {
      patchSetId = new PatchSet.Id();
      jobId = new LabelId();
    }

    public Key(PatchSet.Id ps, LabelId c) {
      this.patchSetId = ps;
      this.jobId = c;
    }

    @Override
    public PatchSet.Id getParentKey() {
      return patchSetId;
    }

    public LabelId getLabelId() {
      return jobId;
    }

    @Override
    public com.google.gwtorm.client.Key<?>[] members() {
      return new com.google.gwtorm.client.Key<?>[] {jobId};
    }
  }

  @Column(id = 1, name = Column.NONE)
  protected Key key;

  @Column(id = 2)
  protected short value;

  @Column(id = 3)
  protected Timestamp granted;

  @Column(id = 4, notNull = false, length = 255)
  protected String url;

  @Column(id = 5, notNull = false, length = 255)
  protected String reporter;

  @Column(id = 6, notNull = false, length = 255)
  protected String comment;

  @Column(id = 7, notNull = false, length = 255)
  protected String category;

  @Column(id = 8, notNull = false, length = 255)
  protected String duration;

  @Column(id = 9)
  protected boolean abstain;

  @Column(id = 10, notNull = false, length = 255)
  protected String name;

  @Column(id = 11)
  protected boolean rerun;

  protected PatchSetVerification() {}

  public PatchSetVerification(PatchSetVerification.Key k, short v, Timestamp ts) {
    key = k;
    setValue(v);
    setGranted(ts);
  }

  public PatchSetVerification.Key getKey() {
    return key;
  }

  public PatchSet.Id getPatchSetId() {
    return key.patchSetId;
  }

  public LabelId getJobId() {
    return key.jobId;
  }

  public short getValue() {
    return value;
  }

  public void setValue(short v) {
    value = v;
  }

  public boolean getAbstain() {
    return abstain;
  }

  public void setAbstain(boolean a) {
    abstain = a;
  }

  public boolean getRerun() {
    return rerun;
  }

  public void setRerun(boolean r) {
    rerun = r;
  }

  public Timestamp getGranted() {
    return granted;
  }

  public void setGranted(Timestamp ts) {
    granted = ts;
  }

  public String getJob() {
    return getJobId().get();
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getReporter() {
    return reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append('[')
        .append(key)
        .append(": ")
        .append(value)
        .append(']')
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof PatchSetVerification) {
      PatchSetVerification p = (PatchSetVerification) o;
      return Objects.equals(key, p.key)
          && Objects.equals(value, p.value)
          && Objects.equals(granted, p.granted);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, granted);
  }
}
