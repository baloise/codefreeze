
/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

var fieldSelectorUtils =
{

    ajaxData : [],
    initalizeFieldSelector: function(){
        AJS.$("#code-freeze-jira-field-selector").auiSelect2({
            minimumInputLength: 3,
            multiple: true,
            ajax: {
                url: AJS.contextPath()+"/rest/codefreeze/1.0/jiraproxy/field", // JIRA-relative URL to the REST end-point
                type: "GET",
                dataType: "json",
                cache: true,
                // query parameters for the remote ajax call
                data: function data(term) {
                    return {
                        filter: term
                    };
                },
                // parse data from the server into form select2 expects
                results: function results(data) {
                    if (!$.trim(data)) {
                        return {
                            results: []
                        };
                    } else {
                        return {
                            results: data
                        };
                    }
                },
                params: {
                    error: function (response) {
                        if(response.status == 401){
                            AJS.messages.error({
                                title: "Authorization to jira required!",
                                body: "<p> Follow  <a href=\""+response.responseJSON.errors[0].authenticationOption+"\">Jira authentication link</a> </p>",
                                closeable: true
                            });

                            //scroll up to error message
                            location.href = "#";
                            location.href = "#aui-message-bar";
                        }
                    }
                }
            },
            // define how selected element should look like
            formatSelection: function formatSelection(field) {
                return Select2.util.escapeMarkup(field.text);
            },
            // define message showed when there are no matches
            formatNoMatches: function formatNoMatches(query) {
                return "No matches found";
            }
        });
    },
    setPreselectedFields: function(){
        var preselectedFieldsContainer = $("#issueCheckerFields");
        if(preselectedFieldsContainer!= null && preselectedFieldsContainer.text() != null && preselectedFieldsContainer.length > 0) {
            $("#code-freeze-jira-field-selector").auiSelect2('data', JSON.parse(preselectedFieldsContainer.text()));
        }
    },
    matchCustom: function (params, data) {
        // If there are no search terms, return all of the data
        if ($.trim(params.term) === '') {
            return data;
        }

        // Do not display the item if there is no 'text' property
        if (typeof data.text === 'undefined') {
            return null;
        }

        // `params.term` should be the term that is used for searching
        // `data.text` is the text that is displayed for the data object
        if (data.text.indexOf(params.term) > -1) {
            var modifiedData = $.extend({}, data, true);
            // You can return modified objects from here
            // This includes matching the `children` how you want in nested data sets
            return modifiedData;
        }

        // Return `null` if the term should not be displayed
        return null;
    }
};

//$("#test").select2({multiple: true, data: [{id: "test", text: "text"}]});
AJS.$(window).load(function () {
    fieldSelectorUtils.initalizeFieldSelector();
    fieldSelectorUtils.setPreselectedFields();
})