

/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

/*
Objects passed in to the script:
issue - issue object as retrieved from the jira rest service. Contains basic properties (id, key)
and array of fields that were set on the configuration script
toRef - reference to which pull request points to

return nonEmpty string upon failing validation which will be displayed.
*/
package groovy

import com.google.gson.JsonObject


def isFixVersionCorrectToTargetBranch(issue, toRef){
    //do some calculation
    return true
}

if(issue.fields.fixVersions.size() != 1) {
    return "Only one fix version allowed."
}

if(!isFixVersionCorrectToTargetBranch(issue, toRef)){
    return "Incorrect fix version for target branch"
}

return null
