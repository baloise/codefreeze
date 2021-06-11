/*
 * Copyright 2021 Baloise Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.baloise.open.bitbucket.common.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class ScriptHelper {
    public static String validateScript(String script) {
        GroovyShell sh = new GroovySandbox().createSandbox(new Binding());
        String errorMessages = null;
        try {
            sh.parse(script);
        } catch (CompilationFailedException exception) {
            errorMessages = exception.getMessage();
        }
        return errorMessages;
    }

    public static Object executeScript(String script, HashMap<String, Object> variables) throws ScriptExecutionException {
        return executeScript(script, variables, false);
    }

    public static Object executeScript(String script, HashMap<String, Object> variables, Boolean bypassSecurity) throws ScriptExecutionException {
        Object executionResult = null;
        Binding groovyBinding = new Binding();
        variables.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                groovyBinding.setVariable(s, o);
            }
        });

        GroovyShell sh = new GroovySandbox().createSandbox(groovyBinding, !bypassSecurity);
        try {
            executionResult = sh.evaluate(script);
        } catch (Exception exception) {
            throw new ScriptExecutionException("Error during script execution", exception);
        }
        return executionResult;
    }
}
