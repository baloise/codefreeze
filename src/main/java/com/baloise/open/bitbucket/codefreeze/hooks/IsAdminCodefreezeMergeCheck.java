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
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeCheck;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component("isAdminCodefreezeMergeCheck")
public class IsAdminCodefreezeMergeCheck implements RepositoryMergeCheck {

    private final PermissionService permissionService;
    private final CodeFreezeSettingDao codeFreezeSettingDao;

    @Autowired
    public IsAdminCodefreezeMergeCheck(@ComponentImport PermissionService permissionService, CodeFreezeSettingDao codeFreezeSettingDao) {
        this.permissionService = permissionService;
        this.codeFreezeSettingDao = codeFreezeSettingDao;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull PullRequestMergeHookRequest request) {
        PullRequest pullRequest = request.getPullRequest();
        Repository repository = pullRequest.getToRef().getRepository();
        if (!permissionService.hasRepositoryPermission(repository, Permission.REPO_ADMIN)) {
            CodeFreezeSetting codeFreezeSetting = codeFreezeSettingDao.getCodeFreezeSettings(repository);

            Boolean isBranchFrozen = FrozenBranchUtil.isBranchFrozen(request.getToRef().getDisplayId(), codeFreezeSetting);
            if (isBranchFrozen) {
                if (pullRequest.getReviewers().stream().noneMatch(reviewer -> reviewer.isApproved() &&
                        permissionService.hasRepositoryPermission(reviewer.getUser(), repository, Permission.REPO_ADMIN))) {
                    return RepositoryHookResult.rejected("Codefreeze", "CodeFreeze is in force. RM needs to accept changes.");
                }
            }
        }

        return RepositoryHookResult.accepted();
    }

}
