/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.common.groovy;


import com.baloise.open.bitbucket.common.json.JsonParser;
import com.baloise.open.bitbucket.common.files.FileUtils;
import com.google.gson.JsonObject;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.Test;

public class GroovySandboxTest {

    private FileUtils fileUtils = new FileUtils();

    @Test
    public void testParseFromString(){
        FileUtils fileUtils = new FileUtils();

        String script = fileUtils.loadFileAsString("/groovy/ParseScript.groovy");
        String JSON = fileUtils.loadFileAsString("/json/issueResponse.json");
        JsonObject json = JsonParser.parseJsonObject(JSON);
        Binding binding = new Binding();
        binding.setVariable("issue", json);
        binding.setVariable("fromRef", "10/2/0/GALRE-64718");
        binding.setVariable("toRef", "10/2/0/master");
        GroovyShell sh = new GroovySandbox().createSandbox(binding);
        String result = (String)sh.evaluate(script);
        assert(result != null);
    }

    @Test
    public void testParseFromStringHappyPath(){

        String script = fileUtils.loadFileAsString("/groovy/ParseScript.groovy");
        String JSON = fileUtils.loadFileAsString("/json/issueResponseSingleFix.json");
        JsonObject json = JsonParser.parseJsonObject(JSON);
        Binding binding = new Binding();
        binding.setVariable("issue", json);
        binding.setVariable("fromRef", "10/2/0/GALRE-64718");
        binding.setVariable("toRef", "10/2/0/master");
        GroovyShell sh = new GroovySandbox().createSandbox(binding);
        String result = (String)sh.evaluate(script);
        assert(result == null);
    }
}
