/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.codefreeze.hooks;

import com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSetting;
import com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSettingDao;
import com.baloise.open.bitbucket.codefreeze.util.FrozenBranchUtil;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryPushHookRequest;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.RefChangeType;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component("IsAfterCodeFreezeHook")
public class IsAfterCodeFreezeHook implements PreRepositoryHook<RepositoryPushHookRequest> {

    private final PermissionService permissionService;
    private final CodeFreezeSettingDao codeFreezeSettingDao;

    private final String frozenMsg = "Branch is frozen create proper pull request" +
            " and contact nearest Release Manager!";



    @Autowired
    public IsAfterCodeFreezeHook(@ComponentImport PermissionService permissionService,
                                 CodeFreezeSettingDao codeFreezeSettingDao) {
        this.permissionService = permissionService;
        this.codeFreezeSettingDao = codeFreezeSettingDao;
    }


    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext preRepositoryHookContext, @Nonnull RepositoryPushHookRequest request) {
        final Repository repo = request.getRepository();
        final CodeFreezeSetting codeFreezeSetting = codeFreezeSettingDao.getCodeFreezeSettings(repo);

//        if(!permissionService.hasRepositoryPermission(repo,Permission.REPO_ADMIN)) {
            Boolean isFrozen = request.getRefChanges().stream()
                    .anyMatch(refChange ->
                            refChange.getType() == RefChangeType.UPDATE
                                    && FrozenBranchUtil.isBranchFrozen(refChange.getRef().getDisplayId(), codeFreezeSetting));

            if (isFrozen) {
                request.getScmHookDetails().ifPresent(scmDetails -> {
                    scmDetails.out().println(frozenMsg);
                });
                return RepositoryHookResult.rejected(frozenMsg, frozenMsg);
            }
//        }
        return RepositoryHookResult.accepted();
    }
}
