/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.codefreeze.rest;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.rest.util.ResourcePatterns;
import com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSetting;
import com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSettingDao;
import com.baloise.open.bitbucket.codefreeze.persistence.BranchFreeze;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
@Path(ResourcePatterns.REPOSITORY_URI + "/branchfreeze")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CodeFreezeResource {

    private final com.baloise.open.bitbucket.codefreeze.persistence.CodeFreezeSettingDao CodeFreezeSettingDao;
    private final PermissionService PermissionService;

    @Autowired
    public CodeFreezeResource(CodeFreezeSettingDao CodeFreezeSettingDao, PermissionService PermissionService) {
        this.CodeFreezeSettingDao = CodeFreezeSettingDao;
        this.PermissionService = PermissionService;
    }

    @GET
    @Path("/")
    public List<BranchFreezeModel> getBranchFreezes(@Context Repository repo) {
        CodeFreezeSetting codeFreezeSetting = CodeFreezeSettingDao.getCodeFreezeSettings(repo);
        BranchFreeze[] frozenBranches = {};
        if (codeFreezeSetting != null) {
            frozenBranches = codeFreezeSetting.getBranchFreezes();
        }
        return Arrays.stream(frozenBranches)
                .map(BranchFreezeModel::new)
                .collect(Collectors.toList());
    }

    @POST
    public BranchFreezeModel createBranchFreeze(@Context Repository repo, BranchFreezeModel newBranchFreeze) {
        if (PermissionService.hasRepositoryPermission(repo, Permission.REPO_ADMIN)) {
            return createOrUpdateBranchFreeze(repo, newBranchFreeze);
        } else {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity("Insufficient permissions")
                    .build());
        }
    }

    //PUT method for a create action is incorrect, but is kept for backwards compatibility
    @PUT
    public BranchFreezeModel createBranchFreezePUT(@Context Repository repo, BranchFreezeModel newBranchFreeze) {
        if (PermissionService.hasRepositoryPermission(repo, Permission.REPO_ADMIN)) {
            return createOrUpdateBranchFreeze(repo, newBranchFreeze);
        } else {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity("Insufficient permissions")
                    .build());
        }
    }

    @Path("/{branchFreezeId}")
    @DELETE
    public void removeBranchFreeze(@Context Repository repo, @PathParam("branchFreezeId") String branchfreezeId) {
        if (PermissionService.hasRepositoryPermission(repo, Permission.REPO_ADMIN)) {
            CodeFreezeSetting CodeFreezeSetting = CodeFreezeSettingDao.getCodeFreezeSettings(repo);
            if (CodeFreezeSetting == null) {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity("Webhook not found")
                        .build());
            }
            if (CodeFreezeSetting.getRepositoryId().equals(repo.getId())) {
                CodeFreezeSettingDao.deleteBranchFreeze(Integer.valueOf(branchfreezeId));
            }
        } else {
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity("Insufficient permissions")
                    .build());
        }
    }

    private BranchFreezeModel createOrUpdateBranchFreeze(Repository repo,
                                                         BranchFreezeModel updatedBranchFreeze) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsedDate;
        try{
            parsedDate = sdf.parse(updatedBranchFreeze.getDate());
        }catch (ParseException e){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Date wrong format")
                    .build());
        }

        BranchFreeze createdBranchFreeze = CodeFreezeSettingDao.createOrUpdateBranchFreeze(repo, updatedBranchFreeze.getBranch(), parsedDate);
        return new BranchFreezeModel(createdBranchFreeze);
    }
}
