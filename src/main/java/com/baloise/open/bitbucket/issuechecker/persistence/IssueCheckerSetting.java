/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.issuechecker.persistence;

import net.java.ao.*;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;


@Table("IssueCheckerConfig")
@Preload
public interface IssueCheckerSetting extends Entity{
    String COLUMN_REPO_ID = "REPO_ID";
    String COLUMN_ISSUE_FIELDS = "FIELDS";
    String COLUMN_SCRIPT = "SCRIPT";
    String COLUMN_IS_SCRIPT_LIMITED = "IS_SCRIPT_LIMITED";

    @Accessor(COLUMN_REPO_ID)
    @Indexed
    @NotNull
    Integer getRepositoryId();

    @OneToMany
    BranchIssueChecker[] getBranches();

    @Accessor(COLUMN_ISSUE_FIELDS)
    @StringLength(StringLength.UNLIMITED)
    String getIssueFields();

    @Mutator(COLUMN_ISSUE_FIELDS)
    void setIssueFields(String issueFields);

    @Accessor(COLUMN_SCRIPT)
    @StringLength(StringLength.UNLIMITED)
    String getScript();

    @Mutator(COLUMN_SCRIPT)
    void setScript(String script);

    @Accessor(COLUMN_IS_SCRIPT_LIMITED)
    Boolean getIsScriptLimited();

    @Mutator(COLUMN_IS_SCRIPT_LIMITED)
    void setIsScriptLimited(Boolean isScriptLimited);

    @OneToMany
    WhiteListUser[] getWhiteListUsers();

    @OneToMany
    WhiteListGroup[] getWhiteListGroups();
}
