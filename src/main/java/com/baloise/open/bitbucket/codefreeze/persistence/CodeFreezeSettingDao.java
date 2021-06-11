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

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.baloise.open.bitbucket.codefreeze.persistence.BranchFreeze.COLUMN_DATE;
import static com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSetting.COLUMN_REPO_ID;
import static com.baloise.open.bitbucket.codefreeze.persistence.BranchFreeze.COLUMN_CODE_FREEZE_SETTINGS_ID;
import static com.baloise.open.bitbucket.codefreeze.persistence.BranchFreeze.COLUMN_BRANCHNAME;

@Component
public class CodeFreezeSettingDao{
    private final ActiveObjects activeObjects;

    @Autowired
    public CodeFreezeSettingDao(@ComponentImport ActiveObjects activeObjects){
        this.activeObjects = activeObjects;
    }

    public CodeFreezeSetting getCodeFreezeSettings(Repository repo){

        CodeFreezeSetting[] results = activeObjects.find(CodeFreezeSetting.class,
                Query.select().where(COLUMN_REPO_ID + " = ?", repo.getId()));
        return results.length == 1 ? results[0]  : null;
    }

    public CodeFreezeSetting createOrUpdateCodeFreezeSetting(Repository repo){
        CodeFreezeSetting codeFreezeSetting = getCodeFreezeSettings(repo);
        if(codeFreezeSetting == null){
            codeFreezeSetting = activeObjects.create(CodeFreezeSetting.class, ImmutableMap.<String, Object>builder()
            .put(COLUMN_REPO_ID, repo.getId())
                    .build());
        }
        return codeFreezeSetting;
    }

    public BranchFreeze createOrUpdateBranchFreeze(Repository repo, String branch, Date date){
        CodeFreezeSetting codeFreezeSetting = createOrUpdateCodeFreezeSetting(repo);

        BranchFreeze[] branchFreezes = activeObjects.find(BranchFreeze.class,
                Query.select().where(COLUMN_CODE_FREEZE_SETTINGS_ID + " = ?" +
                                " AND "
                                + COLUMN_BRANCHNAME + " = ?" ,
                        codeFreezeSetting.getID(),
                        branch
                        ));

        BranchFreeze branchFreeze = null;
        if(branchFreezes.length > 0){
            branchFreeze = branchFreezes[0];
        }

        if(branchFreeze == null){
            branchFreeze = activeObjects.create(BranchFreeze.class, ImmutableMap.<String, Object>builder()
            .put(COLUMN_CODE_FREEZE_SETTINGS_ID, codeFreezeSetting.getID())
            .put(COLUMN_BRANCHNAME, branch)
            .put(COLUMN_DATE, date)
            .build());
        }
        branchFreeze.setDate(date);
        branchFreeze.save();
        return branchFreeze;
    }

    public void deleteBranchFreeze(Repository repo, String branch){
        CodeFreezeSetting codeFreezeSetting = createOrUpdateCodeFreezeSetting(repo);
        BranchFreeze branchFreeze = activeObjects.find(BranchFreeze.class,
                Query.select().where(COLUMN_CODE_FREEZE_SETTINGS_ID + " = ?" +
                                " AND "
                                + COLUMN_BRANCHNAME + " = ?" ,
                        codeFreezeSetting.getID(),
                        branch
                ))[0];

        activeObjects.delete(branchFreeze);
    }

    public BranchFreeze getBranchFreeze(Integer ID){
        BranchFreeze[] branchFreezes = activeObjects.find(BranchFreeze.class,
                Query.select().where("ID" + " = ?" ,
                        ID
                ));
        return branchFreezes.length > 0 ? branchFreezes[0] : null;
    }

    public void deleteBranchFreeze(Integer branchID){
        activeObjects.delete(getBranchFreeze(branchID));
    }
}
