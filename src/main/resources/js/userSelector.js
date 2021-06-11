/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

var codeFrezeUserSelector = {
    parseGroupsDataIntoSelect2: function (groups) {
        var results = []
        groups.forEach(function (group) {
            results.push({id: group.name, text: group.name})
        })
        return results
    },
    initializeUserSelector: function () {
        AJS.$("#code-freeze-user-selector").auiSelect2({
            minimumInputLength: 3,
            multiple: true,
            ajax: {
                url: AJS.contextPath() + "/rest/api/1.0/admin/users", // JIRA-relative URL to the REST end-point
                type: "GET",
                dataType: 'json',
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
                            results: data.values
                        };
                    }
                }
            },
            // define how selected element should look like
            formatSelection: function formatSelection(user) {
                return Select2.util.escapeMarkup(user.displayName);
            },
            // define how single option should look like
            formatResult: function formatResult(user, container, query, escapeMarkup) {
                // format result string
                var resultText = user.displayName + " - (" + user.name + ")";
                var higlightedMatch = [];
                // we need this to disable html escaping by select2 as we are doing it on our own
                var noopEscapeMarkup = function noopEscapeMarkup(s) {
                    return s;
                };
                // highlight matches of the query term using matcher provided by the select2 library
                Select2.util.markMatch(escapeMarkup(resultText), escapeMarkup(query.term), higlightedMatch, noopEscapeMarkup);
                // convert array to string
                higlightedMatch = higlightedMatch.join("");
                // return avatarHtml + higlightedMatch;
                return higlightedMatch
            },
            // define message showed when there are no matches
            formatNoMatches: function formatNoMatches(query) {
                return "No matches found";
            }
        });
    },
    initializeGroupSelector: function () {
        AJS.$("#code-freeze-group-selector").auiSelect2({
            minimumInputLength: 3,
            multiple: true,
            ajax: {
                url: AJS.contextPath() + "/rest/api/1.0/admin/groups", // JIRA-relative URL to the REST end-point
                type: "GET",
                dataType: 'json',
                cache: true,
                // query parameters for the remote ajax call
                data: function data(term) {
                    return {
                        filter: term
                    };
                },
                // parse data from the server into form select2 expects
                results: function results(data) {
                    return {
                        results: codeFrezeUserSelector.parseGroupsDataIntoSelect2(data.values)
                    };
                }
            },
            // define how selected element should look like
            formatSelection: function formatSelection(group) {
                return Select2.util.escapeMarkup(group.id);
            },
            // define message showed when there are no matches
            formatNoMatches: function formatNoMatches(query) {
                return "No matches found";
            }
        });
    },
    setPreselectedUsers: function () {
        $("#code-freeze-user-selector").auiSelect2('data', JSON.parse($("#issueCheckerWhiteListUsers").text()));
    },
    setPreselectedGroups: function () {
        $("#code-freeze-group-selector").auiSelect2('data', JSON.parse($("#issueCheckerWhiteListGroups").text()));
    }
}


//Executed to late
// AJS.toInit(function(){
//     codeFreezeUtils = {whiteListUsers: [],
//         whiteListGroups: [],
//         jiraFields: []
//     };
//     initializeUserSelector();
//     initializeGroupSelector();
//     initalizeVariablesFromFields();
//     setPreselectedUsers()
// });

AJS.$(window).load(function () {
    codeFrezeUserSelector.initializeUserSelector();
    codeFrezeUserSelector.initializeGroupSelector();
    codeFrezeUserSelector.setPreselectedUsers();
    codeFrezeUserSelector.setPreselectedGroups();
})
