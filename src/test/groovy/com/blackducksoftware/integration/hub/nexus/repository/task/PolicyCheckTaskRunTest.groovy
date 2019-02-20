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
package com.blackducksoftware.integration.hub.nexus.repository.task

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.sonatype.nexus.proxy.repository.Repository

import com.blackducksoftware.integration.hub.nexus.application.IntegrationInfo
import com.blackducksoftware.integration.hub.nexus.repository.task.walker.TaskWalker
import com.blackducksoftware.integration.hub.nexus.test.TestWalker
import com.blackducksoftware.integration.hub.nexus.util.ParallelEventProcessor

public class PolicyCheckTaskRunTest {
    private TestWalker walker
    private TaskWalker taskWalker
    private IntegrationInfo integrationInfo
    private ParallelEventProcessor parallelEventProcessor

    @Before
    public void initTest() {
        walker = new TestWalker()
        taskWalker = new TaskWalker(walker)

        integrationInfo = Mockito.mock(IntegrationInfo.class)
        parallelEventProcessor = Mockito.mock(ParallelEventProcessor.class)

        Mockito.doNothing().when(parallelEventProcessor).shutdownProcessor()
    }

    private List<Repository> createRepositoryList(int count) {
        List<Repository> repositoryList = new ArrayList<>()
        for(int index = 0; index < count; index++) {
            Repository repository = [ getName: {-> "repository_"+count}] as Repository
            repositoryList.add(repository)
        }

        return repositoryList
    }

    @Test
    public void testWalkingContext() {
        int count = 2
        PolicyCheckTask policyCheckTask = new PolicyCheckTask(taskWalker, null, parallelEventProcessor, integrationInfo) {
                    @Override
                    protected List<Repository> createRepositoryList() {
                        return createRepositoryList(count)
                    }
                }
        policyCheckTask.doRun()
        Assert.assertTrue(walker.hasContexts())
        Assert.assertEquals(count, walker.getContextList().size())
    }

    @Test
    public void testNoRepos() {
        int count = 0
        PolicyCheckTask policyCheckTask = new PolicyCheckTask(taskWalker, null, parallelEventProcessor, integrationInfo) {
                    @Override
                    protected List<Repository> createRepositoryList() {
                        return createRepositoryList(count)
                    }
                }
        policyCheckTask.doRun()
        Assert.assertFalse(walker.hasContexts())
    }
}
