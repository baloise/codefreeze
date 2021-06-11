/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.issuechecker.rest;

import com.baloise.open.bitbucket.issuechecker.persistence.IssueCheckerSetting;
import com.atlassian.bitbucket.user.UserService;
import com.google.gson.Gson;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

@XmlRootElement
public class IssueCheckerSettingModel {
    @XmlElement
    private Integer ID;

    @XmlElement
    private String script;

    @XmlElement
    private BranchIssueCheckerModel[] branches;

    @XmlElement
    private WhiteListUserModel[] whiteListUsers;

    @XmlElement
    private WhiteListGroupModel[] whiteListGroups;

    @XmlElement
    private Boolean isScriptLimited;

    public Integer getID() {
        return ID;
    }

    public String getScript() {
        return script;
    }

    public BranchIssueCheckerModel[] getBranches() {
        return branches;
    }

    public WhiteListUserModel[] getWhiteListUsers() {
        return whiteListUsers;
    }

    public WhiteListGroupModel[] getWhiteListGroups(){
        return whiteListGroups;
    }

    public Boolean getIsScriptLimited() {
        return isScriptLimited;
    }
    public IssueCheckerSettingModel(IssueCheckerSetting issueCheckerSetting, UserService userService){
        this.ID = issueCheckerSetting.getID();
        this.script = issueCheckerSetting.getScript();
        if(this.script == null){
            this.script = "";
        }
        this.branches = Arrays.stream(issueCheckerSetting.getBranches()).map(BranchIssueCheckerModel::new).toArray(BranchIssueCheckerModel[]::new);
        this.whiteListGroups = Arrays.stream(issueCheckerSetting.getWhiteListGroups()).map(WhiteListGroupModel::new).toArray(WhiteListGroupModel[]::new);
        this.whiteListUsers = Arrays.stream(issueCheckerSetting.getWhiteListUsers()).map(whiteListUser -> {return new WhiteListUserModel(whiteListUser, userService);}).toArray(WhiteListUserModel[]::new);
        this.isScriptLimited = issueCheckerSetting.getIsScriptLimited();
    }

    @Override
    public String toString() {
        return "IssueCheckerSettingModel{" +
                "ID=" + ID +
                ", script='" + script + '\'' +
                ", branches=" + Arrays.toString(branches) +
                ", whiteListUsers=" + Arrays.toString(whiteListUsers) +
                '}';
    }

    public String toJsonString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
