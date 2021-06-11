/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.codefreeze.condition;

import com.atlassian.bitbucket.hook.repository.RepositoryHook;
import com.atlassian.bitbucket.hook.repository.RepositoryHookService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.scope.RepositoryScope;
import com.atlassian.bitbucket.scope.Scope;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.Condition;

import java.util.Map;

public class IsHookEnabledCondition implements Condition {

    final RepositoryHookService repositoryHookService;

    public IsHookEnabledCondition(@ComponentImport RepositoryHookService repositoryHookService){
        this.repositoryHookService = repositoryHookService;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> params) {
        Boolean mergeCheckActive = Boolean.FALSE;
        Boolean pushHookActive = Boolean.FALSE;

        Repository repo = (Repository) params.get("repository");
        if(repo != null) {
            Scope scope = new RepositoryScope(repo);

            RepositoryHook mergeHook = repositoryHookService.getByKey(scope, "com.baloise.open.bitbucket.codefreeze:isAdminMergeCheck");
            mergeCheckActive = mergeHook != null && mergeHook.isEnabled();

            RepositoryHook pushHook = repositoryHookService.getByKey(scope, "com.baloise.open.bitbucket.codefreeze:IsAfterCodeFreezeHook");
            pushHookActive = pushHook != null && pushHook.isEnabled();
        }
        return mergeCheckActive || pushHookActive;
    }
}
