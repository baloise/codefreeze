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

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.*;

@Table("FieldIssueChecker")
@Preload
@Indexes(
        @Index(name = "issuecheckersettingsfield", methodNames = {"getIssueCheckerSetting", "getFieldId"})
)
public interface FieldIssueChecker extends Entity {
    String COLUMN_ISSUE_CHECKER_SETTING_ID = "ISSUE_CHECKER_SETTING_ID";
    String COLUMN_FIELD_ID = "FIELD_ID";
    String COLUMN_FIELD_NAME = "FIELD_NAME";

    IssueCheckerSetting getIssueCheckerSetting();
    void setIssueCheckerSetting(IssueCheckerSetting issueCheckerSetting);

    @Accessor(COLUMN_FIELD_ID)
    @StringLength(40)
    @NotNull
    String getFieldId();

    @Mutator(COLUMN_FIELD_ID)
    void setFieldId(String branchName);

    @Accessor(COLUMN_FIELD_NAME)
    @StringLength(StringLength.MAX_LENGTH)
    @NotNull
    String getFieldName();

    @Mutator(COLUMN_FIELD_NAME)
    void setFieldName(String setFieldName);
}
