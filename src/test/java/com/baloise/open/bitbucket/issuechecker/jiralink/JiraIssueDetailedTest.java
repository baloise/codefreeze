/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.issuechecker.jiralink;


import com.baloise.open.bitbucket.common.json.JsonParser;
import com.google.gson.*;
import org.junit.Assert;
import org.junit.Test;

public class JiraIssueDetailedTest {

    static final String JSON = "{\"expand\":\"renderedFields,names,schema,operations,editmeta,changelog,versionedRepresentations\",\"id\":\"488463\",\"self\":\"https://jira.baloisenet.com/atlassian/rest/api/latest/issue/488463\",\"key\":\"GALRE-64781\",\"fields\":{\"issuetype\":{\"self\":\"https://jira.baloisenet.com/atlassian/rest/api/2/issuetype/3\",\"id\":\"3\",\"description\":\"A task that needs to be done.\",\"iconUrl\":\"https://jira.baloisenet.com/atlassian/secure/viewavatar?size=xsmall&avatarId=33148&avatarType=issuetype\",\"name\":\"Task\",\"subtask\":false,\"avatarId\":33148},\"fixVersions\":[{\"self\":\"https://jira.baloisenet.com/atlassian/rest/api/2/version/105645\",\"id\":\"105645\",\"description\":\"Fliesst nicht ins Sprint Reporting vom PMO ein.\",\"name\":\"NotInReport\",\"archived\":false,\"released\":false}],\"customfield_12250\":\"1|hyhfie:\"}}";

    @Test
    public void testParseFromString(){
        JsonObject json = JsonParser.parseJsonObject(JSON);
        JsonObject fields = json.getAsJsonObject("fields");

        Assert.assertTrue("Expected 1 element in fixVersions array", fields.getAsJsonArray("fixVersions").size() == 1);
    }
}
