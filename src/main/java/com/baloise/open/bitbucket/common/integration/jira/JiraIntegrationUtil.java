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

import com.atlassian.applinks.api.*;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import java.util.List;

public class JiraIntegrationUtil {
    final Logger log = Logger.getLogger(this.getClass().getName());
    private ApplicationLink appLink;
    private ApplicationLinkRequestFactory requestFactory;

    public JiraIntegrationUtil(ApplicationLinkService applicationLinkService) {
        appLink = applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
        requestFactory = appLink.createAuthenticatedRequestFactory(OAuthAuthenticationProvider.class);
    }

    public List<SimpleJiraIssueField> getAvailableFields() throws CredentialsRequiredException {
        ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, getURLForFields());
        List<SimpleJiraIssueField> results = null;
        try {
            results = request.execute(new FieldRetrieveHandler());
        } catch (ResponseException e) {
            log.error(e.getMessage());
        }
        return results;
    }

    public JsonObject getIssueFields(String key, String finalFieldsIds) throws CredentialsRequiredException, ResponseException {
        JsonObject jsonIssue = null;
        ApplicationLinkRequest applicationLinkRequest = requestFactory.createRequest(Request.MethodType.GET, getURLForSpecificIssue(key, finalFieldsIds));
        jsonIssue = applicationLinkRequest.execute(new IssueFieldsRetrieveHandler());
        return jsonIssue;
    }

    private String getURLForFields() {
        return "/rest/api/latest/field";
    }

    private String getURLForSpecificIssue(String issueKey, String fields) {
        String url = "rest/api/latest/issue/" + issueKey;
        //Check for settings of fields
        if (fields != null && !fields.isEmpty()) {
            url = url + "?" + fields;
        }
        return url;
    }


}
