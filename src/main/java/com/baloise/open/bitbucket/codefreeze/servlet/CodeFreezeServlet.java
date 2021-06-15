/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.codefreeze.servlet;

import com.baloise.open.bitbucket.codefreeze.persistence.BranchFreeze;
import com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSetting;
import com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSettingDao;
import com.baloise.open.bitbucket.codefreeze.rest.BranchFreezeModel;
import com.baloise.open.bitbucket.common.servlet.AbstractSimpleServlet;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.util.DateFormatter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;


public class CodeFreezeServlet extends AbstractSimpleServlet {

    RepositoryService repositoryService;
    ProjectService projectService;
    CodeFreezeSettingDao codeFreezeSettingDao;
    PermissionService permissionService;
    DateFormatter dateFormatter;

    Boolean isAdmin;
    Repository repository;
    CodeFreezeSetting codeFreezeSetting;
    Project project;

    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public CodeFreezeServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport RepositoryService repositoryService,
                             @ComponentImport ProjectService projectService, @ComponentImport PermissionService permissionService,
                             CodeFreezeSettingDao codeFreezeSettingDao) {
        super(soyTemplateRenderer);
        this.repositoryService = repositoryService;
        this.projectService = projectService;
        this.codeFreezeSettingDao = codeFreezeSettingDao;
        this.permissionService = permissionService;
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        if(isSettings){
//            codeFreezeSettingDao.addOrUpdateBranchFreeze(repository, "9/0/0/master", new Date());
//        }
//        String template = isSettings ? ".projectSettings" : "plugin.example.project";

        req.getLocale();
        ImmutableMap.Builder<String, Object> parameters = ImmutableMap.<String, Object>builder();
        parameters.put("project", project)
                .put("repository", repository);

        if (req.getParameter("edit") != null) {
            if (!isAdmin) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
            String template = "codefreeze.templates.branchEdit";

            if (req.getParameter("id") != null) {
                BranchFreeze branchFreeze = codeFreezeSettingDao.getBranchFreeze(Integer.valueOf(req.getParameter("id")));
                BranchFreezeModel branchFreezeModel = new BranchFreezeModel(branchFreeze);
                parameters.put("branch", branchFreezeModel);
            }

            render(resp, template, parameters.build());
        } else {
            if (req.getParameter("delete") != null) {
                if (!isAdmin) {
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    Integer idToDelete = Integer.valueOf(req.getParameter("id"));
                    codeFreezeSettingDao.deleteBranchFreeze(idToDelete);
                    resp.sendRedirect(req.getRequestURL().toString());
                }
            } else {
                String template = "codefreeze.templates.repositorySettings";
                BranchFreezeModel[] branchFreezes = convertBranchFreezesToModels(codeFreezeSetting.getBranchFreezes());
                render(resp, template, parameters
                        .put("branches", branchFreezes)
                        .put("isAdmin", isAdmin).build());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            Repository repository = getRepository(req);
//        List<NameValuePair> queryParams = URLEncodedUtils.parse(getFullURL(req), "UTF-8");

            CodeFreezeSetting codeFreezeSetting = codeFreezeSettingDao.createOrUpdateCodeFreezeSetting(repository);
            java.util.Date parsedDate;

            try{
                parsedDate = sdf.parse(req.getParameter("date"));
            }catch (ParseException e){
                throw new IOException("Error parsing date");
            }

            codeFreezeSettingDao.createOrUpdateBranchFreeze(repository, req.getParameter("branch"),
                    parsedDate);

            BranchFreezeModel[] branchFreezes = convertBranchFreezesToModels(codeFreezeSetting.getBranchFreezes());
            resp.sendRedirect(req.getRequestURL().toString());
            //        render(resp, template, ImmutableMap.<String, Object>builder()
//                .put("repository", repository)
//                .put("branches", branchFreezes)
//                .put("isAdmin",isAdmin).build());
        }
    }

    public static URI getFullURL(HttpServletRequest request) throws ServletException {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        String url;
        if (queryString == null) {
            url = requestURL.toString();

        } else {
            url = requestURL.append('?').append(queryString).toString();
        }
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }
    }

    private Repository getRepository(HttpServletRequest req) throws IOException {
        // Get repoSlug from path
        String pathInfo = req.getPathInfo();

        String[] components = pathInfo.split("/");

        if (components.length < 3) {
            return null;
        }

        Repository repository = repositoryService.getBySlug(components[1], components[2]);
        if (repository == null) {
            return null;
        }
        return repository;
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
        codeFreezeSetting = codeFreezeSettingDao.createOrUpdateCodeFreezeSetting(repository);
        isAdmin = permissionService.hasRepositoryPermission(repository, Permission.REPO_ADMIN);

        super.service(req, resp);
    }

    private BranchFreezeModel[] convertBranchFreezesToModels(BranchFreeze[] branchFreezes) {
        List<Object> list = Arrays.stream(branchFreezes).map(branchFreeze -> new BranchFreezeModel(branchFreeze)).collect(Collectors.toList());
        return list.toArray(new BranchFreezeModel[list.size()]);
    }
}
