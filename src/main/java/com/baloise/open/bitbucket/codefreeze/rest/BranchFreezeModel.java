/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.codefreeze.rest;


import com.baloise.open.bitbucket.codefreeze.persistence.BranchFreeze;
import org.apache.http.client.utils.DateUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BranchFreezeModel {
    @XmlElement
    private Integer ID;

    @XmlElement
    private String branch;

    @XmlElement
    private String date;

    public BranchFreezeModel(){
    }

    public BranchFreezeModel(BranchFreeze branchFreeze){
        ID = branchFreeze.getID();
        branch = branchFreeze.getBranchName();
        date = DateUtils.formatDate(branchFreeze.getDate(), "YYYY-MM-dd");
    }


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "BranchFreezeModel{" +
                ", branch='" + branch + '\'' +
                ", date=" + date +
                '}';
    }
}
