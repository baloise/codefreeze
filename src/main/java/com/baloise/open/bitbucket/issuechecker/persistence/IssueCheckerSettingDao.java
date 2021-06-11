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

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class IssueCheckerSettingDao {
    private final ActiveObjects activeObjects;
    private final UserService userService;

    @Autowired
    public IssueCheckerSettingDao(@ComponentImport ActiveObjects activeObjects, @ComponentImport UserService userService) {
        this.activeObjects = activeObjects;
        this.userService = userService;
    }

    public IssueCheckerSetting getIssueCheckerSettings(Repository repo) {

        IssueCheckerSetting[] results = activeObjects.find(IssueCheckerSetting.class,
                Query.select().where(IssueCheckerSetting.COLUMN_REPO_ID + " = ?", repo.getId()));
        return results.length == 1 ? results[0] : null;
    }

    public IssueCheckerSetting getOrCreateIssueCheckerSetting(Repository repo) {
        IssueCheckerSetting IssueCheckerSetting = getIssueCheckerSettings(repo);
        if (IssueCheckerSetting == null) {
            IssueCheckerSetting = activeObjects.create(IssueCheckerSetting.class, ImmutableMap.<String, Object>builder()
                    .put(com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSetting.COLUMN_REPO_ID, repo.getId())
                    .build());
        }
        return IssueCheckerSetting;
    }

    /**
     * @param id id of the branch to delete
     * @return true upon successful removal of the branch
     */
    public boolean deleteBranchIssueChecker(Integer id) {
        BranchIssueChecker[] branchIssueChecker = activeObjects.find(BranchIssueChecker.class,
                Query.select().where("ID = ?",
                        id
                ));

        if (branchIssueChecker.length == 0) {
            return false;
        } else {
            try {
                activeObjects.delete(branchIssueChecker[0]);
                return true;
            }catch(Exception e){
                return false;
            }
        }
    }

    public void saveUserWhitelist(Repository repo, List<String> userIDs) {
        IssueCheckerSetting issueCheckerSetting = getOrCreateIssueCheckerSetting(repo);

        List<String> existingIds = Arrays.stream(issueCheckerSetting.getWhiteListUsers()).map(WhiteListUser::getUserID).collect(Collectors.toList());
        List<String> idsToRemove = new ArrayList<>(existingIds);
        List<String> idsToAdd = new ArrayList<>(userIDs);
        idsToRemove.removeIf(s -> userIDs.contains(s));

        idsToAdd.removeIf(s -> existingIds.contains(s) || s.isEmpty());

        if (!idsToRemove.isEmpty()) {
            activeObjects.deleteWithSQL(WhiteListUser.class, WhiteListUser.COLUMN_ISSUE_CHECKER_SETTING_ID + " = ?" +
                            " AND " + WhiteListUser.COLUMN_USER_ID + " IN (?)", issueCheckerSetting.getID(),
                    String.join(",", idsToRemove));
        }
        for (String userId : idsToAdd) {
            WhiteListUser newObj = activeObjects.create(WhiteListUser.class, ImmutableMap.<String, Object>builder()
                    .put(WhiteListUser.COLUMN_ISSUE_CHECKER_SETTING_ID, issueCheckerSetting.getID())
                    .put(WhiteListUser.COLUMN_USER_ID, userId)
                    .build());
        }
    }

    public void saveGroupWhitelist(Repository repository, List<String> groupNames) {
        IssueCheckerSetting issueCheckerSetting = getOrCreateIssueCheckerSetting(repository);

        List<String> existingNames = Arrays.stream(issueCheckerSetting.getWhiteListGroups()).map(WhiteListGroup::getName).collect(Collectors.toList());
        List<String> namesToRemove = new ArrayList<>(existingNames);
        List<String> groupToAdd = new ArrayList<>(groupNames);
        namesToRemove.removeIf(s -> groupNames.contains(s));

        groupToAdd.removeIf(s -> existingNames.contains(s) || s.isEmpty());

        if (!namesToRemove.isEmpty()) {
            activeObjects.deleteWithSQL(WhiteListGroup.class, WhiteListGroup.COLUMN_ISSUE_CHECKER_SETTING_ID + "= ?" +
                            " AND " + WhiteListGroup.COLUMN_NAME + " IN (?)", issueCheckerSetting.getID(),
                    String.join(",", namesToRemove));
        }
        for (String groupName : groupToAdd) {
            WhiteListGroup newObj = activeObjects.create(WhiteListGroup.class, ImmutableMap.<String, Object>builder()
                    .put(WhiteListGroup.COLUMN_ISSUE_CHECKER_SETTING_ID, issueCheckerSetting.getID())
                    .put(WhiteListGroup.COLUMN_NAME, groupName)
                    .build());
        }
    }

    public void saveScript(Repository repository, String scriptParam) {

        if (scriptParam != null && !scriptParam.isEmpty()) {

        }
        IssueCheckerSetting issueCheckerSetting = getOrCreateIssueCheckerSetting(repository);
        issueCheckerSetting.setScript(scriptParam);
        issueCheckerSetting.save();
    }

    public void saveFields(Repository repository, String fieldsIds) {
        IssueCheckerSetting issueCheckerSetting = getOrCreateIssueCheckerSetting(repository);
        issueCheckerSetting.setIssueFields(fieldsIds);
        issueCheckerSetting.save();
    }

    public void saveBranch(String newBranchParam, Repository repository) {
        IssueCheckerSetting issueCheckerSetting = getOrCreateIssueCheckerSetting(repository);
        activeObjects.create(BranchIssueChecker.class, ImmutableMap.<String, Object>builder()
                .put(BranchIssueChecker.COLUMN_ISSUE_CHECKER_SETTING_ID, issueCheckerSetting.getID())
                .put(BranchIssueChecker.COLUMN_BRANCHNAME, newBranchParam)
                .build());
    }
}
