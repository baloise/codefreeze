/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

var codeFreezeBranchDialog = {
    // Shows the dialog when the "Show dialog" button is clicked
    showDialogFunction: function () {
        AJS.$("#add-branch-button").click(function (e) {
            e.preventDefault();
            AJS.dialog2("#add-branch-dialog").show();
        });
    },
    // Hides the dialog
    submitDialogFunction: function () {
        var currentClick = AJS.$("#add-branch-dialog-submit-button").click;

        AJS.$("#add-branch-dialog-submit-button").click(function (e) {
            currentClick(e);
            AJS.$("#newbranchInput").text = "";
            AJS.dialog2("#add-branch-dialog").hide();
        });
    },
    cancelDialogFunction: function(){
        AJS.$("#dialog-cancel-button").click(function (e) {
            e.preventDefault();
            AJS.dialog2("#add-branch-dialog").hide();
            AJS.$("#newbranchInput").text = "";
        });
    },
    removeBranchButtonsInit: function(){
        AJS.$(".remove-branch-button").click(codeFreezeBranchDialog.removeBranchButtonFunction)
    },
    removeBranchButtonFunction : function(e){
        var branchID = $(this).prop("name");
        AJS.$.ajax({
            url: AJS.contextPath()+"/rest/codefreeze/1.0/issuechecker/branch/"+branchID,
            type: "DELETE",
            data: ({}),
            dataType: "json",
            success: function(msg){
                location.reload();
            }
        });
        AJS.$("#BranchRow"+branchID).remove();
    }
}

AJS.$(window).load(function () {
    codeFreezeBranchDialog.showDialogFunction();
    codeFreezeBranchDialog.submitDialogFunction();
    codeFreezeBranchDialog.cancelDialogFunction();
    codeFreezeBranchDialog.removeBranchButtonsInit();
})