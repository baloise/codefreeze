/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.issuechecker.servlet;

import com.baloise.open.bitbucket.common.files.FileUtils;
import com.baloise.open.bitbucket.common.groovy.ScriptHelper;
import com.baloise.open.bitbucket.common.integration.jira.JiraIntegrationUtil;
import com.baloise.open.bitbucket.common.integration.jira.SimpleJiraIssueField;
import com.baloise.open.bitbucket.common.servlet.AbstractSimpleServlet;
import com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSetting;
import com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSettingDao;
import com.baloise.open.bitbucket.issuechecker.rest.IssueCheckerSettingModel;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class IssueCheckerServlet extends AbstractSimpleServlet {
    RepositoryService repositoryService;
    ProjectService projectService;
    IssueCheckerSettingDao issueCheckerSettingDao;
    PermissionService permissionService;
    UserService userService;
    ApplicationLinkService applicationLinkService;

    Boolean isAdmin;
    Repository repository;
    IssueCheckerSetting issueCheckerSetting;
    Project project;

    private final static String TEMPLATE = "issuechecker.templates.repositorySettings";
    private final static String EXAMPLE_SCRIPT = IssueCheckerServlet.readExampleFile();

    public IssueCheckerServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport RepositoryService repositoryService,
                               @ComponentImport ProjectService projectService, @ComponentImport PermissionService permissionService,
                               @ComponentImport UserService userService,
                               @ComponentImport ApplicationLinkService applicationLinkService,
                               IssueCheckerSettingDao issueCheckerSettingDao) {
        super(soyTemplateRenderer);
        this.repositoryService = repositoryService;
        this.projectService = projectService;
        this.issueCheckerSettingDao = issueCheckerSettingDao;
        this.permissionService = permissionService;
        this.userService = userService;
        this.applicationLinkService = applicationLinkService;
    }

    private ImmutableMap.Builder<String, Object> appendCommonFields(ImmutableMap.Builder<String, Object> parameters) {
        ApplicationLink appLink = applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
        Gson gson = new Gson();

        if (appLink != null) {
            parameters.put("jiraLink", appLink.getDisplayUrl().toASCIIString());
            String fieldsIds = issueCheckerSetting.getIssueFields();
            if (fieldsIds != null && !fieldsIds.isEmpty()) {
                try {
                    parameters.put("jiraFieldsJson", gson.toJson(retrieveFieldsByIds(fieldsIds)));
                } catch (CredentialsRequiredException e) {
                    parameters.put("error", "Authorisation is required for IssueCheckerPlugin please follow:\n"+
                            "<a href="+e.getAuthorisationURI()+">"+e.getAuthorisationURI()+"</a>");
                }
            }
        }

        IssueCheckerSettingModel settingModel = new IssueCheckerSettingModel(issueCheckerSetting, userService);

        parameters.put("project", project)
                .put("repository", repository)
                .put("setting", settingModel)
                .put("userWhiteListJson", gson.toJson(settingModel.getWhiteListUsers()))
                .put("exampleScript", EXAMPLE_SCRIPT)
                .put("groupWhiteListJson", gson.toJson(settingModel.getWhiteListGroups()));

        return parameters;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ImmutableMap.Builder<String, Object> parameters = ImmutableMap.<String, Object>builder();

        appendCommonFields(parameters);

        render(resp, TEMPLATE, parameters
                .build());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getLocale();
        ImmutableMap.Builder<String, Object> parameters = ImmutableMap.<String, Object>builder();

        Boolean encounteredError = false;

        if (!isAdmin) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            appendCommonFields(parameters);

            String userParam = req.getParameter("users");
            String groupParam = req.getParameter("groups");
            String scriptParam = req.getParameter("script");
            String fieldParam = req.getParameter("fields");
            String newBranchParam = req.getParameter("newbranch");

            if (userParam != null) {
                parameters.put("users", userParam);
                String[] userSelectorParam = userParam.split(",");
                issueCheckerSettingDao.saveUserWhitelist(repository, Arrays.asList(userSelectorParam));
            }
            if (groupParam != null) {
                parameters.put("groups", groupParam);
                String[] groupSelectorParam = groupParam.split(",");
                issueCheckerSettingDao.saveGroupWhitelist(repository, Arrays.asList(groupSelectorParam));
            }
            if (scriptParam != null) {
                if (!scriptParam.isEmpty()) {
                    parameters.put("script", scriptParam);
                    String validationResult = ScriptHelper.validateScript(scriptParam);
                    if (validationResult == null || validationResult.isEmpty()) {
                        issueCheckerSettingDao.saveScript(repository, scriptParam);
                    } else {
                        encounteredError = true;
                        parameters.put("scriptError", validationResult);
                    }
                }
            }
            if (fieldParam != null) {
                parameters.put("fields", fieldParam);
                issueCheckerSettingDao.saveFields(repository, fieldParam);
            }
            if (newBranchParam != null) {
                issueCheckerSettingDao.saveBranch(newBranchParam, repository);
            }

            if (encounteredError) {
                render(resp, TEMPLATE, parameters
                        .build());
            } else {
                //reload the page for best results
                resp.sendRedirect(req.getRequestURL().toString());
            }
        }
    }

    private List<SimpleJiraIssueField> retrieveFieldsByIds(String issueFields) throws CredentialsRequiredException {
        JiraIntegrationUtil jiraIntegrationUtil = new JiraIntegrationUtil(applicationLinkService);
        List<SimpleJiraIssueField> results = jiraIntegrationUtil.getAvailableFields();
        List<String> ids = Arrays.asList(issueFields.split(","));
        results.removeIf(simpleJiraIssueField -> !ids.contains(simpleJiraIssueField.getId()));
        return results;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get projectKey from path
        String pathInfo = req.getPathInfo();
        String[] components = pathInfo.split("/");

        if (components.length < 2) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        project = projectService.getByKey(components[1]);
        if (project == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        repository = repositoryService.getBySlug(components[1], components[2]);
        issueCheckerSetting = issueCheckerSettingDao.getOrCreateIssueCheckerSetting(repository);
        isAdmin = permissionService.hasRepositoryPermission(repository, Permission.REPO_ADMIN);

        super.service(req, resp);
    }

    private static String readExampleFile() {
        String fileContent = new FileUtils().loadFileAsString("/examples/ParseScript.groovy");
        if (fileContent != null) {
            return fileContent;
        } else {
            return "";
        }
    }

}
