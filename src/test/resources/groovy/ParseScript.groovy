/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package groovy

import com.google.gson.JsonObject

//For running script without sandbox

//if(!binding.hasVariable("issue")) {
//    String JSON = new File(getClass().getResource('/json/issueResponseSingleFix.json').toURI()).text
//    JsonObject json = JsonParser.parse(JSON);
//    binding.setVariable("issue", json);
//    binding.setVariable("fromRef", "10/2/0/GALRE-64718");
//    binding.setVariable("toRef", "10/2/0/master");
//}
//END

def getExpectedFixVersion(String toRef){
    def extractedBranchNo = (toRef =~ /(\d+\\/){3,4}/)[0][0]
    def digits = extractedBranchNo.split("/")
    if(digits.size() < 4){
        digits += ["0"]
    }
    return "GWR_IS ${digits.join(".")}".toString()
}

def getFixVersion(JsonObject issue){
    String fixVersion = issue.fields.fixVersions[0].name.getAsString()
    return fixVersion
}

if(issue.fields.fixVersions == null){
    return "No fix version set".toString()
}

if(issue.fields.fixVersions.size() != 1) {
    return "Only one fix version allowed.".toString()
}

if(getExpectedFixVersion(toRef) != getFixVersion(issue)){
    return "Wrong fix version. Expected: ${getExpectedFixVersion(toRef)}, Actual : ${getFixVersion(issue)}".toString()
}

return null
