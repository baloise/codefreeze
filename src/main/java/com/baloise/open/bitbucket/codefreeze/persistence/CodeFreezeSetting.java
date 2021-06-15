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
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;


@Table("FreezeConfig")
@Preload
public interface CodeFreezeSetting extends Entity{
    String COLUMN_REPO_ID = "REPO_ID";

    @Accessor(COLUMN_REPO_ID)
    @Indexed
    @NotNull
    Integer getRepositoryId();

    @OneToMany
    BranchFreeze[] getBranchFreezes();
}
