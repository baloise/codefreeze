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

import com.baloise.open.bitbucket.common.json.JsonParser;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;


import java.util.ArrayList;
import java.util.List;

public class FieldRetrieveHandler implements ApplicationLinkResponseHandler<List<SimpleJiraIssueField>> {
    final Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Override
    public List<SimpleJiraIssueField> credentialsRequired(Response response) throws ResponseException {
        return null;
    }

    @Override
    public List<SimpleJiraIssueField> handle(Response response) throws ResponseException {
        String responseBody  = response.getResponseBodyAsString();
        if(response.getStatusCode() != 200){
            throw new ResponseException("Response invalid");
        }
        List<SimpleJiraIssueField> simpleJiraIssueFields = new ArrayList<>();
        JsonArray ja = JsonParser.parseJsonArray(responseBody);
        ja.forEach(jsonElement -> {
            if(jsonElement.isJsonObject()){
                JsonObject jsonObject  = jsonElement.getAsJsonObject();
                try {
                    SimpleJiraIssueField issueField = new SimpleJiraIssueField(jsonObject.get("id").getAsString(), jsonObject.get("name").getAsString());
                    simpleJiraIssueFields.add(issueField);
                }catch (Exception e){
                    log.error("Error parsing fields:"+jsonObject.toString()+"\n"+
                            e.getMessage());
                }
            }
        });
        return simpleJiraIssueFields;
    }
}
