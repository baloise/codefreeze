/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.issuechecker.rest;

import com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSetting;
import com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSettingDao;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.rest.util.ResourcePatterns;
import com.atlassian.bitbucket.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


@Component
@Path("/issuechecker")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IssueCheckerResource {
    private final com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSettingDao IssueCheckerSettingDao;
    private final com.atlassian.bitbucket.permission.PermissionService PermissionService;
    private final UserService UserService;

    @Autowired
    public IssueCheckerResource(IssueCheckerSettingDao IssueCheckerSettingDao, PermissionService PermissionService, UserService UserSetting) {
        this.IssueCheckerSettingDao = IssueCheckerSettingDao;
        this.PermissionService = PermissionService;
        this.UserService = UserSetting;
    }

    @GET
    @Path("/"+ ResourcePatterns.REPOSITORY_URI)
    public IssueCheckerSettingModel get(@Context Repository repo) {
        IssueCheckerSetting issueCheckerSetting = IssueCheckerSettingDao.getIssueCheckerSettings(repo);
        return new IssueCheckerSettingModel(issueCheckerSetting, UserService);
    }

    @DELETE
    @Path("/branch/{id}")
    public boolean deleteBranch(@PathParam("id") String branchId) {
        Boolean wasSuccess = null;
        try {
            Integer branchIdNumber = Integer.parseInt(branchId);
            IssueCheckerSettingDao.deleteBranchIssueChecker(Integer.parseInt(branchId));
            wasSuccess = true;
        } catch (NumberFormatException e) {
            wasSuccess = false;
        }
        return wasSuccess;
    }
}
