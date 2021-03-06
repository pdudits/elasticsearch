/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.AnalysisRegistry;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.similarity.SimilarityService;
import org.elasticsearch.indices.IndicesModule;
import org.elasticsearch.indices.mapper.MapperRegistry;
import org.elasticsearch.test.IndexSettingsModule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;


public class MapperTestUtils {

    public static MapperService newMapperService(Path tempDir, Settings indexSettings) throws IOException {
        IndicesModule indicesModule = new IndicesModule();
        return newMapperService(tempDir, indexSettings, indicesModule);
    }

    public static MapperService newMapperService(Path tempDir, Settings settings, IndicesModule indicesModule) throws IOException {
        Settings.Builder settingsBuilder = Settings.builder()
            .put("path.home", tempDir)
            .put(settings);
        if (settings.get(IndexMetaData.SETTING_VERSION_CREATED) == null) {
            settingsBuilder.put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT);
        }
        Settings finalSettings = settingsBuilder.build();
        MapperRegistry mapperRegistry = indicesModule.getMapperRegistry();
        IndexSettings indexSettings = IndexSettingsModule.newIndexSettings(new Index("test"), finalSettings);
        AnalysisService analysisService = new AnalysisRegistry(null, new Environment(finalSettings)).build(indexSettings);
        SimilarityService similarityService = new SimilarityService(indexSettings, Collections.emptyMap());
        return new MapperService(indexSettings,
            analysisService,
            similarityService,
            mapperRegistry,
            () -> null);
    }
}
