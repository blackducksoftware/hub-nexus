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
package com.blackducksoftware.integration.hub.nexus.repository.walker;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.attributes.Attributes;
import org.sonatype.nexus.proxy.attributes.DefaultAttributesHandler;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.local.fs.DefaultFSLocalRepositoryStorage;
import org.sonatype.sisu.goodies.common.Loggers;

import com.blackducksoftware.integration.hub.builder.HubScanConfigBuilder;
import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.model.request.ProjectRequest;
import com.blackducksoftware.integration.hub.request.builder.ProjectRequestBuilder;
import com.blackducksoftware.integration.hub.scan.HubScanConfig;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.hub.util.ProjectNameVersionGuess;
import com.blackducksoftware.integration.hub.util.ProjectNameVersionGuesser;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;

public class ArtifactScanner {

    final Logger logger = Loggers.getLogger(ArtifactScanner.class);
    final int HUB_SCAN_MEMORY = 4096;
    final boolean HUB_SCAN_DRY_RUN = false;

    private final HubServerConfig hubServerConfig;
    private final HubServicesFactory hubServicesFactory;
    private final StorageItem item;
    private final Repository repository;
    private final ResourceStoreRequest request;
    private final DefaultAttributesHandler attributesHandler;

    public ArtifactScanner(final HubServerConfig hubServerConfig, final HubServicesFactory hubServicesFactory, final Repository repository, final ResourceStoreRequest request, final StorageItem item,
            final DefaultAttributesHandler attributesHandler) {
        this.hubServerConfig = hubServerConfig;
        this.hubServicesFactory = hubServicesFactory;
        this.repository = repository;
        this.item = item;
        this.request = request;
        this.attributesHandler = attributesHandler;
    }

    public void scan() {
        try {
            logger.info("Beginning scan of artifact");
            final HubScanConfig scanConfig = createScanConfig();
            logger.info(String.format("Scan Path %s", scanConfig.getScanTargetPaths()));
            final CLIDataService cliDataService = createCLIDataService(hubServicesFactory);
            final ProjectRequest projectRequest = createProjectRequest();
            // TODO: Fix file paths. do not perform the scan the file paths do not exist causes scan to run in the hub for a long time.
            // final ProjectVersionView projectVersionView = cliDataService.installAndRunControlledScan(hubServerConfig, scanConfig, projectRequest, true, IntegrationInfo.DO_NOT_PHONE_HOME);
            final Attributes itemAtt = item.getRepositoryItemAttributes();

            itemAtt.put("lastScanned", String.valueOf(System.currentTimeMillis()));
            attributesHandler.storeAttributes(item);

        } catch (final Exception ex) {
            logger.error("Error occurred during scan", ex);
        }
    }

    private ProjectRequest createProjectRequest() {
        final ProjectRequestBuilder builder = new ProjectRequestBuilder();
        final ProjectNameVersionGuess nameVersionGuess = generateProjectNameVersion(item);
        builder.setProjectName(nameVersionGuess.getProjectName());
        builder.setVersionName(nameVersionGuess.getVersionName());
        builder.setProjectLevelAdjustments(true);
        builder.setPhase("Development");
        builder.setDistribution("External");
        return builder.build();
    }

    private ProjectNameVersionGuess generateProjectNameVersion(final StorageItem item) {
        final String path = item.getParentPath();

        final ProjectNameVersionGuesser nameVersionGuesser = new ProjectNameVersionGuesser();
        final ProjectNameVersionGuess nameVersionGuess = nameVersionGuesser.guessNameAndVersion(FilenameUtils.removeExtension(item.getName()));
        String name = nameVersionGuess.getProjectName();
        String version = nameVersionGuess.getVersionName();

        final String[] pathSections = path.split("/");
        if (pathSections.length > 1) {
            version = pathSections[pathSections.length - 1];
            name = pathSections[pathSections.length - 2];
        }

        final ProjectNameVersionGuess nameVersion = new ProjectNameVersionGuess(name, version);
        return nameVersion;
    }

    private HubScanConfig createScanConfig() throws IOException {
        final HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder();
        hubScanConfigBuilder.setScanMemory(HUB_SCAN_MEMORY);
        hubScanConfigBuilder.setDryRun(HUB_SCAN_DRY_RUN);
        final File blackduckDir = new File("/sonatype-work/blackduck");
        blackduckDir.mkdirs();
        final File toolsDir = new File(blackduckDir, "tools");
        hubScanConfigBuilder.setToolsDir(toolsDir);
        hubScanConfigBuilder.setWorkingDirectory(blackduckDir);
        hubScanConfigBuilder.disableScanTargetPathExistenceCheck();

        final DefaultFSLocalRepositoryStorage storage = (DefaultFSLocalRepositoryStorage) repository.getLocalStorage();
        final File repositoryPath = storage.getFileFromBase(repository, request);
        final File file = new File(repositoryPath, item.getPath());
        hubScanConfigBuilder.addScanTargetPath(file.getCanonicalPath());

        return hubScanConfigBuilder.build();
    }

    private CLIDataService createCLIDataService(final HubServicesFactory hubServicesFactory) {
        final IntLogger intLogger = new Slf4jIntLogger(logger);
        final CLIDataService cliDataService = hubServicesFactory.createCLIDataService(intLogger);
        return cliDataService;
    }
}
