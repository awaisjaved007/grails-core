/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.web.servlet.mvc;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MissingPropertyException;
import org.codehaus.groovy.grails.commons.GrailsControllerClass;
import org.codehaus.groovy.grails.web.servlet.mvc.exceptions.ControllerExecutionException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Implements action invokation throught Method
 *
 * @author Stephane Maldini
 * @since 1.4
 */
public class MethodGrailsControllerHelper extends AbstractGrailsControllerHelper {


    @Override
    protected Method retrieveAction(GroovyObject controller, String actionName, HttpServletResponse response) {
        Method action;
        try {
            action = (Method) controller.getClass().getMethod(actionName);
            if(Modifier.isPrivate(action.getModifiers())){
                throw new NoSuchMethodException();
            }
        } catch (NoSuchMethodException mpe) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            } catch (IOException e) {
                throw new ControllerExecutionException("I/O error sending 404 error", e);
            }
        }
        return action;
    }

    @Override
    protected Object invoke(GroovyObject controller, Object action) {
        try {
            return ((Method)action).invoke(controller);
        } catch (Exception e) {
            throw new ControllerExecutionException("Runtime error executing action", e);
        }
    }
}
