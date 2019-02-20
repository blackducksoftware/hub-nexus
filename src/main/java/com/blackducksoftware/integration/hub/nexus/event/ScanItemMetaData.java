/*
 * hub-nexus
 *
 * 	Copyright (C) 2019 Black Duck Software, Inc.
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

import java.util.Map;

import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.StorageItem;

import com.blackducksoftware.integration.hub.model.request.ProjectRequest;

public class ScanItemMetaData {
    private final StorageItem item;
    private final ResourceStoreRequest request;
    private final Map<String, String> taskParameters;
    private final ProjectRequest projectRequest;

    public ScanItemMetaData(final StorageItem item, final ResourceStoreRequest request, final Map<String, String> taskParameters, final ProjectRequest projectRequest) {
        this.item = item;
        this.request = request;
        this.taskParameters = taskParameters;
        this.projectRequest = projectRequest;
    }

    public StorageItem getItem() {
        return item;
    }

    public ResourceStoreRequest getRequest() {
        return request;
    }

    public Map<String, String> getTaskParameters() {
        return taskParameters;
    }

    public ProjectRequest getProjectRequest() {
        return projectRequest;
    }
}
