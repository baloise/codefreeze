/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.issuechecker.hooks;

import com.baloise.open.bitbucket.common.groovy.ScriptExecutionException;
import com.baloise.open.bitbucket.common.groovy.ScriptHelper;
import com.baloise.open.bitbucket.common.integration.jira.JiraIntegrationUtil;
import com.baloise.open.bitbucket.issuechecker.persistence.BranchIssueChecker;
import com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSetting;
import com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSettingDao;
import com.baloise.open.bitbucket.issuechecker.persistence.WhiteListGroup;
import com.atlassian.applinks.api.*;
import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.PullRequestMergeHookRequest;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryMergeCheck;
import com.atlassian.bitbucket.integration.jira.JiraIssueService;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestRef;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.StandardRefType;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;

@Component("IsIssueKeyCorrect")
public class IsIssueKeyCorrect implements RepositoryMergeCheck {
    final Logger log = Logger.getLogger(this.getClass().getName());

    private final PermissionService permissionService;
    private final IssueCheckerSettingDao issueCheckerSettingDao;
    private final JiraIssueService jiraIssueService;
    private final ApplicationLinkService applicationLinkService;
    private final UserService userService;
    private final AuthenticationContext authenticationContext;

    private static final String SHORTMSG = "Jira issues not meeting criteria";


    @Autowired
    public IsIssueKeyCorrect(@ComponentImport PermissionService permissionService,
                             IssueCheckerSettingDao issueCheckerSettingDao, @ComponentImport JiraIssueService jiraIssueService,
                             @ComponentImport ApplicationLinkService applicationLinkService,
                             @ComponentImport UserService userService, @ComponentImport AuthenticationContext authenticationContext) {
        this.permissionService = permissionService;
        this.issueCheckerSettingDao = issueCheckerSettingDao;
        this.jiraIssueService = jiraIssueService;
        this.applicationLinkService = applicationLinkService;
        this.userService = userService;
        this.authenticationContext = authenticationContext;
    }


    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull PullRequestMergeHookRequest request) {
        PullRequest pullRequest = request.getPullRequest();
        Repository repository = pullRequest.getToRef().getRepository();
        Repository repo = request.getRepository();
        IssueCheckerSetting settings = issueCheckerSettingDao.getIssueCheckerSettings(repo);
        BranchIssueChecker[] branches = null;

        if (settings != null) {
            branches = settings.getBranches();
        }
        PullRequestRef targetRef = pullRequest.getToRef();
        Map<String, Collection<String>> validationErrors = null;

        Boolean mergeCheckConfigured = settings != null &&
                (settings.getScript() != null || settings.getScript().isEmpty());

        if (mergeCheckConfigured && userIsNotWhitelisted(authenticationContext.getCurrentUser(), repo) && targetRef.getType() == StandardRefType.BRANCH && isBranchProtected(targetRef)) {
            java.util.Set<com.atlassian.bitbucket.integration.jira.JiraIssue> issues
                    = jiraIssueService.getIssuesForPullRequest(repository.getId(), pullRequest.getId());

            validationErrors = (validateIssues(issues, request.getFromRef().getDisplayId(), request.getToRef().getDisplayId(), settings));
        }

        if(validationErrors!= null && !validationErrors.isEmpty()){
            StringJoiner joiner = new StringJoiner("\n");
            validationErrors.forEach((key, message) -> joiner.add(key + ":" + message));
            return RepositoryHookResult.rejected(SHORTMSG, joiner.toString());
        }
        return RepositoryHookResult.accepted();
    }

    private boolean userIsNotWhitelisted(ApplicationUser currentUser, Repository repository) {
        IssueCheckerSetting issueCheckerSetting = issueCheckerSettingDao.getOrCreateIssueCheckerSetting(repository);
        if (Arrays.asList(issueCheckerSetting.getWhiteListUsers()).stream().anyMatch(whiteListUser -> whiteListUser.getUserID() == Integer.toString(currentUser.getId()))) {
            return false;
        }
        for (WhiteListGroup whiteListGroup : issueCheckerSetting.getWhiteListGroups()) {
            if (userService.isUserInGroup(currentUser, whiteListGroup.getName())) {
                return false;
            }
        }
        return true;
    }


    private Boolean isBranchProtected(PullRequestRef pullRequestRef) {
        String branch = pullRequestRef.getDisplayId();
        IssueCheckerSetting setting = issueCheckerSettingDao.getIssueCheckerSettings(pullRequestRef.getRepository());
        return Arrays.stream(setting.getBranches()).anyMatch(branchIssueChecker -> branch.matches(branchIssueChecker.getBranchName()));
    }

    private Map<String, Collection<String>> validateIssues(java.util.Set<com.atlassian.bitbucket.integration.jira.JiraIssue> issues, String fromRef, String toRef, IssueCheckerSetting settings) {

        String fields = settings.getIssueFields();
        Map<String, Collection<String>> validationErrors = new HashMap<>();

        if (fields == null || fields.isEmpty()) {
            fields = "fixVersions";
        }

        final String finalFieldsIds = fields;
        issues.forEach(jiraIssue -> {
            Collection<String> errorMessages = new ArrayList<>();
            try {
                JiraIntegrationUtil jiraIntegrationUtil = new JiraIntegrationUtil(applicationLinkService);
                JsonObject jsonIssue = jiraIntegrationUtil.getIssueFields(jiraIssue.getKey(), finalFieldsIds);

                HashMap<String, Object> variables = new HashMap<>();
                variables.put("issue", jsonIssue);
                variables.put("fromRef", fromRef);
                variables.put("toRef", toRef);
                String result = null;
                Boolean isScriptLimited = settings.getIsScriptLimited() != null && settings.getIsScriptLimited() == true;
                try {
                    log.debug("issue:"+ jiraIssue.getKey()+"fromRef:"+fromRef+" toRef:"+toRef);
                    log.debug("Script:\n"+settings.getScript());
                    result = (String) ScriptHelper.executeScript(settings.getScript(), variables, !isScriptLimited);
                } catch (ScriptExecutionException scriptError) {
                    errorMessages.add(scriptError.getMessage());
                    log.error(scriptError.toString());
                }
                if (result != null && !result.isEmpty()) {
                    errorMessages.add(result);
                }
            } catch (CredentialsRequiredException e) {
                log.error("Authorisation error");
                errorMessages.add("Authorisation is required for IssueCheckerPlugin please follow:\n" +
                        " " + e.getAuthorisationURI());
            } catch (ResponseException e) {
                errorMessages.add("Could not validate");
                log.error(e.toString());
            }
            if (!errorMessages.isEmpty()) {
                validationErrors.put(jiraIssue.getKey(), errorMessages);
            }
        });
        return validationErrors;
    }
}
