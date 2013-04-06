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

package org.apache.camel.itest.osgi.bean.validator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.itest.osgi.OSGiIntegrationTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import static org.ops4j.pax.exam.OptionUtils.combine;

@RunWith(JUnit4TestRunner.class)
public class BeanValidatorTest extends OSGiIntegrationTestSupport {

    @Test
    public void testBeanValidatorRoute() throws Exception {
        Exchange exchange = template.request("bean-validator://x", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(createCar("BMW", "DD-AB-123"));
            }
        });

        assertNotNull(exchange);
    }

    @Test
    public void validateShouldSuccessWithExpliciteDefaultGroup() throws Exception {
        Exchange exchange = template.request("bean-validator://x?group=javax.validation.groups.Default", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(createCar("BMW", "DD-AB-123"));
            }
        });

        assertNotNull(exchange);
    }

    Car createCar(String manufacturer, String licencePlate) {
        return new CarWithAnnotations(manufacturer, licencePlate);
    }
    
    @Configuration
    public static Option[] configure() {
        Option[] options = combine(
            getDefaultCamelKarafOptions(),
            // using the features to install the other camel components             
            loadCamelFeatures("camel-bean-validator"));
        
        return options;
    }
}