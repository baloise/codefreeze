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

import com.baloise.open.bitbucket.issuechecker.persistence.BranchIssueChecker;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BranchIssueCheckerModel {
    @XmlElement
    private Integer id;

    @XmlElement
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BranchIssueCheckerModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public BranchIssueCheckerModel(BranchIssueChecker branchIssueChecker){
        this.id = branchIssueChecker.getID();
        this.name = branchIssueChecker.getBranchName();
    }
}
