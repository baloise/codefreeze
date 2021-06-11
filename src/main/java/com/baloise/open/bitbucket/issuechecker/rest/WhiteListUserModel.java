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

import com.baloise.open.bitbucket.issuechecker.persistence.WhiteListUser;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;

import javax.xml.bind.annotation.XmlElement;

public class WhiteListUserModel {
    @XmlElement
    private Integer id;

    @XmlElement
    private String slug;

    @XmlElement
    private String displayName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "WhiteListUserModel{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    WhiteListUserModel(WhiteListUser whiteListUser, UserService userService) {
        try {
            ApplicationUser user = userService.getUserById(Integer.parseInt(whiteListUser.getUserID()));
            this.id = user.getId();
            this.displayName = user.getDisplayName();
            this.slug = user.getSlug();
        } catch (Exception e) {
            this.displayName = "ERROR";
            this.slug = "ERROR";
        }
    }

}
