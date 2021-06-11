/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.codefreeze.persistence;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.*;

import java.util.Date;


@Table("BranchFreeze")
@Preload
@Indexes(
        @Index(name = "freezesettingsbranch", methodNames = {"getCodeFreezeSetting", "getBranchName"})
)
public interface BranchFreeze extends Entity {
    String COLUMN_CODE_FREEZE_SETTINGS_ID = "CODE_FREEZE_SETTING_ID";
    String COLUMN_BRANCHNAME = "BRANCHNAME";
    String COLUMN_DATE = "DATE";

    CodeFreezeSetting getCodeFreezeSetting();
    void setCodeFreezeSetting(CodeFreezeSetting codeFreezeSetting);

    @Accessor(COLUMN_BRANCHNAME)
    @StringLength(StringLength.MAX_LENGTH)
    @NotNull
    String getBranchName();

    @Mutator(COLUMN_BRANCHNAME)
    void setBranchName(String branchName);

    @Accessor(COLUMN_DATE)
    Date getDate();

    @Mutator(COLUMN_DATE)
    void setDate(Date codefreezeDate);
}
