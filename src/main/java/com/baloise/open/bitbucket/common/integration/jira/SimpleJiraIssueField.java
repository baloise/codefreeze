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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple container describing field for UI needs
 */
@XmlRootElement
public class SimpleJiraIssueField {

    @XmlElement
    String id;

    @XmlElement
    String text;

    SimpleJiraIssueField(String id, String text){
        this.id = id;
        this.text = text;
    }

    public String getId(){
        return id;
    }

    public String getText(){
        return text;
    }
}
