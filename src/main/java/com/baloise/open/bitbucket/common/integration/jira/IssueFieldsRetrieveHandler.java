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
import com.google.gson.JsonObject;


public class IssueFieldsRetrieveHandler implements ApplicationLinkResponseHandler<JsonObject> {

    @Override
    public JsonObject credentialsRequired(Response response) throws ResponseException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "CredentialsRequiredException");
        return jsonObject;
    }

    @Override
    public JsonObject handle(Response response) throws ResponseException {
        String responseBody  = response.getResponseBodyAsString();
        return JsonParser.parseJsonObject(responseBody);
    }
}
