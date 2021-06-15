/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.common.integration.jira;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.bitbucket.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Component
@Path("/jiraproxy")
@Produces(MediaType.APPLICATION_JSON)
public class JiraProxyResource {
    private final PermissionService PermissionService;
    private final UserService UserService;
    private final ApplicationLinkService ApplicationLinkService;

    @Autowired
    public JiraProxyResource(PermissionService PermissionService, UserService UserSetting, ApplicationLinkService applicationLinkService) {
        this.PermissionService = PermissionService;
        this.UserService = UserSetting;
        this.ApplicationLinkService = applicationLinkService;
    }

    @GET
    @Path("/field")
    public Response get(@QueryParam("filter") String filter) throws CredentialsRequiredException {
        String filterNormalized;
        if(filter != null){
            filterNormalized = filter.toLowerCase();
        }else{
            filterNormalized = "";
        }

        List<SimpleJiraIssueField> result = null;
        try {
            JiraIntegrationUtil jiraIntegrationUtil = new JiraIntegrationUtil(ApplicationLinkService);
            result = jiraIntegrationUtil.getAvailableFields();
        } catch (CredentialsRequiredException e) {
            Response resp = Response.status(Response.Status.UNAUTHORIZED).contentLocation(e.getAuthorisationURI()).entity("{\"errors\":[{\"status\":\"401\",\"title\":\"Unauthorized\",\"authenticationOption\": \""+e.getAuthorisationURI().toASCIIString()+"\"}]}").build();
            return resp;
        }
        if(!filterNormalized.isEmpty() && result != null) {
            result.removeIf(simpleJiraIssueField -> !simpleJiraIssueField.text.toLowerCase().contains(filterNormalized) && !simpleJiraIssueField.id.toLowerCase().contains(filterNormalized));
        }
        return Response.ok(result).build();
    }
}
