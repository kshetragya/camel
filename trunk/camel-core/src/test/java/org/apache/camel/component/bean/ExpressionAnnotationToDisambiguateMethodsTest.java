/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.bean;

import javax.naming.Context;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Handler;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.Simple;
import org.apache.camel.processor.BeanRouteTest;
import org.apache.camel.util.jndi.JndiContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 
 */
public class ExpressionAnnotationToDisambiguateMethodsTest extends ContextTestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(BeanRouteTest.class);
    protected MyBean myBean = new MyBean();
    protected MyOtherBean myOtherBean = new MyOtherBean();

    public void testSendMessage() throws Exception {
        template.sendBodyAndHeader("direct:in", "<hello>world!</hello>", "foo", "bar");

        assertEquals("bean body: " + myBean, "bar", myBean.bar);
    }

    public void testSendMessageHandler() throws Exception {
        template.sendBodyAndHeader("direct:other", "<hello>world!</hello>", "foo", "bar");

        assertEquals("bean body: " + myOtherBean, "bar", myOtherBean.bar);
    }

    @Override
    protected Context createJndiContext() throws Exception {
        JndiContext answer = new JndiContext();
        answer.bind("myBean", myBean);
        answer.bind("myOtherBean", myOtherBean);
        return answer;
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:in").beanRef("myBean");

                from("direct:other").beanRef("myOtherBean");
            }
        };
    }

    public static class MyBean {
        public String bar;

        public void bar(String body) {
            fail("bar() called with: " + body);
        }

        public void foo(@Simple("${header.foo}") String bar) {
            this.bar = bar;
            LOG.info("foo() method called with: " + bar);
        }

        public void wrongMethod(String body) {
            fail("wrongMethod() called with: " + body);
        }
    }

    public static class MyOtherBean {
        public String bar;

        public void bar(String body) {
            fail("bar() called with: " + body);
        }

        @Handler
        public void foo(@Simple("${header.foo}") String bar) {
            this.bar = bar;
            LOG.info("foo() method called with: " + bar);
        }

        public void wrongMethod(String body) {
            fail("wrongMethod() called with: " + body);
        }
    }
}