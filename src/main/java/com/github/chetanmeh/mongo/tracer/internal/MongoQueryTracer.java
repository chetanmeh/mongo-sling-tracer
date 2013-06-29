/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.chetanmeh.mongo.tracer.internal;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

@Component(metatype = true,policy = ConfigurationPolicy.REQUIRE,description = "%tracer.desc",label = "%tracer.label")
public class MongoQueryTracer {

    @Property(boolValue = false)
    private static String PROP_ENABLED = "enabled";

    private ServiceRegistration filterReg;

    private final Handler mongoLogHandler = new MongoQueryLogHandler();

    private boolean enabled;

    @Activate
    private void activate(BundleContext context, Map<String,Object> config){
        enabled = Boolean.parseBoolean(""+config.get(PROP_ENABLED));

        if(!enabled){
            return;
        }

        Properties p = new Properties();
        p.setProperty("filter.scope","REQUEST");
        filterReg = context.registerService(Filter.class.getName(),new SlingRequestHolderFilter(),p);

        Logger l = getMongoLogger();
        l.addHandler(mongoLogHandler);
        l.setLevel(Level.FINEST);
    }


    @Deactivate
    private void deactivate(){
        getMongoLogger().removeHandler(mongoLogHandler);
        if(filterReg != null){
            filterReg.unregister();
        }
    }

    private Logger getMongoLogger() {
        return Logger.getLogger("com.mongodb.TRACE");
    }


    private static class MongoQueryLogHandler extends Handler {

        @Override
        public void publish(LogRecord record) {
            final ThreadData req = SlingRequestHolderFilter.getThreadLocalData();
            String msg = record.getMessage();
            if(msg.startsWith("Request") && req != null){
                int index = req.counter++;
                req.request.getRequestProgressTracker().log("Mongo {0} {1}",index,msg);
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

    }

    private static class SlingRequestHolderFilter implements Filter {
        private static final ThreadLocal<ThreadData> requestHolder = new ThreadLocal<ThreadData>();

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            final SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;

            try {
                requestHolder.set(new ThreadData(request));
                filterChain.doFilter(request, servletResponse);
            } finally {
                requestHolder.remove();
            }
        }

        @Override
        public void destroy() {
        }

        public static ThreadData getThreadLocalData(){
            return requestHolder.get();
        }
    }

    private static class ThreadData {
        final SlingHttpServletRequest request;
        int counter = 0;

        private ThreadData(SlingHttpServletRequest request) {
            this.request = request;
        }
    }
}
