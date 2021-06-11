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

import com.baloise.open.bitbucket.common.files.FileUtils;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;


public class FieldRetrieveHandlerTest {
    FieldRetrieveHandler testee;

    @Test
    public void handlerTest(){
        testee = new FieldRetrieveHandler();

        List<SimpleJiraIssueField> fields = null;
        try {
            fields =  testee.handle(mockResponse("/json/fieldsResponse.json"));
        } catch (ResponseException e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertEquals("Fields number", 2, fields.size());
        Assert.assertTrue(fields.stream().anyMatch(simpleJiraIssueField -> simpleJiraIssueField.id.equals("customfield_18353")));
        Assert.assertTrue(fields.stream().anyMatch(simpleJiraIssueField -> simpleJiraIssueField.text.equals("Organizations")));
        Assert.assertTrue(fields.stream().anyMatch(simpleJiraIssueField -> simpleJiraIssueField.id.equals("fixVersions")));
        Assert.assertTrue(fields.stream().anyMatch(simpleJiraIssueField -> simpleJiraIssueField.text.equals("Fix Version/s")));

    }

    private Response mockResponse(String filePath){
        Response mockResponse = Mockito.mock(Response.class);
        try {
            Mockito.when(mockResponse.getResponseBodyAsString()).thenReturn(new FileUtils().loadFileAsString(filePath));
            Mockito.when(mockResponse.getStatusCode()).thenReturn(200);
        } catch (ResponseException e) {
            e.printStackTrace();
        }

        return mockResponse;
    }
}
