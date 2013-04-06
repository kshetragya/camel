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
package org.apache.camel.component.freemarker;

import javax.activation.DataHandler;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Freemarker unit test
 */
public class FreemarkerTest extends CamelTestSupport {

    @Test
    public void testVelocityLetter() throws Exception {
        final DataHandler dataHandler = new DataHandler("my attachment", "text/plain");
        Exchange exchange = template.request("direct:a", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().addAttachment("item", dataHandler);
                exchange.getIn().setBody("Monday");
                exchange.getIn().setHeader("name", "Christian");
                exchange.setProperty("item", "7");
            }
        });

        assertEquals("Dear Christian. You ordered item 7 on Monday.", exchange.getOut().getBody());
        assertEquals("Christian", exchange.getOut().getHeader("name"));
        assertSame(dataHandler, exchange.getOut().getAttachment("item"));
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                // START SNIPPET: example
                from("direct:a").
                        to("freemarker:org/apache/camel/component/freemarker/example.ftl");
                // END SNIPPET: example
            }
        };
    }
}