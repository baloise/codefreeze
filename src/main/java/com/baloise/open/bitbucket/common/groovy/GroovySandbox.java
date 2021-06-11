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


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.groovy.sandbox.GroovyValueFilter;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import java.util.HashSet;
import java.util.Set;

public class GroovySandbox {

    private static final Set<Class> BASE_ALLOWED_TYPES = new HashSet<>();
    private static final Set<String> EXTENDED_ALLOWED_TYPES = new HashSet<>();


    public GroovySandbox(){
        setBaseAllowedTypes();
    }

    private void setBaseAllowedTypes() {
        BASE_ALLOWED_TYPES.add(String.class);
        BASE_ALLOWED_TYPES.add(JsonObject.class);
        BASE_ALLOWED_TYPES.add(JsonArray.class);
        BASE_ALLOWED_TYPES.add(JsonPrimitive.class);
        BASE_ALLOWED_TYPES.add(JsonElement.class);
        BASE_ALLOWED_TYPES.add(Integer.class);
        BASE_ALLOWED_TYPES.add(Boolean.class);
        BASE_ALLOWED_TYPES.add(Character.class);

        BASE_ALLOWED_TYPES.add(java.lang.Class.class);
        BASE_ALLOWED_TYPES.add(java.util.regex.Matcher.class);
        BASE_ALLOWED_TYPES.add(java.util.ArrayList.class);
        BASE_ALLOWED_TYPES.add(java.lang.String[].class);
        BASE_ALLOWED_TYPES.add(java.lang.Object[].class);
        BASE_ALLOWED_TYPES.add(org.codehaus.groovy.runtime.GStringImpl.class);
    }

    public GroovyShell createSandbox(Binding binding){
        return createSandbox(binding, true);
    }

    public GroovyShell createSandbox(Binding binding, Boolean useFiltering){
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.addCompilationCustomizers(new SandboxTransformer());
        //new Binding
        GroovyShell sh = new GroovyShell(binding, cc);

        GroovyValueFilter gvf = new GroovyValueFilter(){
            public Object filter(Object o){ return o;}};

        if(useFiltering) {
            gvf = new GroovyValueFilter() {
                public Object filter(Object o) {
                    if (o == null || BASE_ALLOWED_TYPES.contains(o.getClass())) {
                        return o;
                    }
                    if (o instanceof Script || o instanceof Closure) {
                        return o;
                    }
                    if (EXTENDED_ALLOWED_TYPES.contains(o.getClass().getCanonicalName())) {
                        return o;
                    }
                    throw new SecurityException("Denied:" + o.getClass());
                }
            };
        }

        gvf.register();

        return sh;
    }
}
