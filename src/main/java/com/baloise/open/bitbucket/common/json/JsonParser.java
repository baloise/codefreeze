/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.common.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonParser {
    private static JsonElement parse(String jsonString){
        com.google.gson.JsonParser jp = new com.google.gson.JsonParser();
        return jp.parse(jsonString);
    }

    public static JsonArray parseJsonArray(String jsonString){
        return parse(jsonString).getAsJsonArray();
    }

    public static JsonObject parseJsonObject(String jsonString){
        return parse(jsonString).getAsJsonObject();
    }
}
