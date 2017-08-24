/*
 * hub-nexus
 *
 * 	Copyright (C) 2017 Black Duck Software, Inc.
 * 	http://www.blackducksoftware.com/
 *
 * 	Licensed to the Apache Software Foundation (ASF) under one
 * 	or more contributor license agreements. See the NOTICE file
 * 	distributed with this work for additional information
 * 	regarding copyright ownership. The ASF licenses this file
 * 	to you under the Apache License, Version 2.0 (the
 * 	"License"); you may not use this file except in compliance
 * 	with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing,
 * 	software distributed under the License is distributed on an
 * 	"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * 	KIND, either express or implied. See the License for the
 * 	specific language governing permissions and limitations
 * 	under the License.
 */
package com.blackducksoftware.integration.hub.nexus.event;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.nexus.application.HubServiceHelper;
import com.blackducksoftware.integration.hub.nexus.util.HubEventLogger;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HubEventHandler.class)
public class HubEventHandlerTest {

    @Mock
    HubEventLogger hubEventLogger;

    @Mock
    HubServiceHelper hubServiceHelper;

    @Mock
    HubServerConfigBuilder hubServerConfigBuilder;

    // @Test
    // public void createServiceHelperTest() throws Exception {
    // final HubEventHandler hubEventHandler = new HubEventHandler(null);
    //
    // final HubServiceHelper hubServiceHelper = hubEventHandler.createServiceHelper(hubEventLogger, new HashMap<String, String>());
    //
    // Assert.assertNotNull(hubServiceHelper);
    // }
}
