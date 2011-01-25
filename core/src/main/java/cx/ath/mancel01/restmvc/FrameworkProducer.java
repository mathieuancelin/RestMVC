/*
 *  Copyright 2010 Mathieu ANCELIN.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package cx.ath.mancel01.restmvc;

import cx.ath.mancel01.restmvc.http.Session;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mathieu ANCELIN
 */
@RequestScoped
public class FrameworkProducer {

    @Produces @RequestScoped
    public HttpServletRequest getRequest() {
        System.out.println("get req");
        return FrameworkFilter.currentRequest.get();
    }

    @Produces @RequestScoped
    public HttpServletResponse getResponse() {
        return FrameworkFilter.currentResponse.get();
    }

    @Produces
    public Logger getLogger() {
        return FrameworkFilter.logger;
    }

    @Produces @Any @RequestScoped
    public Session getSession() {
        return Session.current.get();
    }
}
