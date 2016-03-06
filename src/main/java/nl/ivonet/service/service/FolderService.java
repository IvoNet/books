/*
 * Copyright 2016 Ivo Woltring <WebMaster@ivonet.nl>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.ivonet.service.service;

import nl.ivonet.service.config.Property;
import nl.ivonet.service.directory.Directory;
import nl.ivonet.service.model.Data;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Ivo Woltring
 */
@Path("/folders")
public class FolderService {

    @Context
    UriInfo uriInfo;

    @Inject
    private Directory directory;

    @Inject
    @Property
    private String rootFolder;

    private Data retrieveData(final String folder) {
        final Data data = new Data(this.directory.folder(folder));
        data.setBaseUri(this.uriInfo.getBaseUriBuilder()
                                    .path(this.getClass())
                                    .build()
                                    .toString());
        data.setBrowseUri(this.uriInfo.getBaseUriBuilder()
                                      .path(this.getClass())
                                      .path("/")
                                      .build()
                                      .toString());
//        data.setFileUri(this.uriInfo.getBaseUriBuilder()
//                                    .path(DownloadService.class)
//                                    .build()
//                                    .toString());
//        data.setDownloadUri(this.uriInfo.getBaseUriBuilder()
//                                        .path(ComicService.class)
//                                        .build()
//                                        .toString());
        return data;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Data root() {
        return retrieveData("");
    }


    @GET
    @Produces(APPLICATION_JSON)
    @Path("/{folder: .+}")
    public Data folder(@PathParam("folder") final String folder) {
        return retrieveData(folder);
    }

}
