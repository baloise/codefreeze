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
import net.java.ao.schema.*;

@Table("BranchIssueChecker")
@Preload
@Indexes(
        @Index(name = "issuecheckersettingsbranch", methodNames = {"getIssueCheckerSetting", "getBranchName"})
)
public interface BranchIssueChecker extends Entity {
    String COLUMN_ISSUE_CHECKER_SETTING_ID = "ISSUE_CHECKER_SETTING_ID";
    String COLUMN_BRANCHNAME = "BRANCHNAME";

    IssueCheckerSetting getIssueCheckerSetting();
    void setIssueCheckerSetting(IssueCheckerSetting issueCheckerSetting);

    @Accessor(COLUMN_BRANCHNAME)
    @StringLength(StringLength.MAX_LENGTH)
    @NotNull
    String getBranchName();

    @Mutator(COLUMN_BRANCHNAME)
    void setBranchName(String branchName);

    @OneToMany
    WhiteListUser[] getWhiteListUsers();

}
