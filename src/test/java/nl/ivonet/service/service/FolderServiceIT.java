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

import nl.ivonet.service.config.BootStrap;
import nl.ivonet.service.directory.Directory;
import nl.ivonet.service.model.Metadata;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.StringReader;
import java.net.URL;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Ivo Woltring
 */
@RunWith(Arquillian.class)
public class FolderServiceIT {

    @ArquillianResource
    private URL base;

    @Deployment
    public static Archive<?> createDeployment() {
        final WebArchive war = ShrinkWrap.create(WebArchive.class)
                                         .addPackage(BootStrap.class.getPackage())
                                         .addPackage(Directory.class.getPackage())
                                         .addPackage(FolderService.class.getPackage())
                                         .addPackage(Metadata.class.getPackage())
                                         .addAsResource("application.properties")
                                         .addAsLibraries(Maven.resolver()
                                                              .loadPomFromFile("pom.xml")
                                                              .importRuntimeDependencies()
                                                              .resolve()
                                                              .withTransitivity()
                                                              .asFile())
                                         .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(war.toString(true));
        return war;


    }


    // {"baseUri":"http://127.0.0.1:8080/1d6a8823-a1eb-44be-91c6-c38c2cfa91e6/api/folders","browseUri":"http://127.0.0.1:8080/1d6a8823-a1eb-44be-91c6-c38c2cfa91e6/api/folders/","fileUri":null,"downloadUri":null,"folder":{"folders":["Stoker, Bram","Twain, Mark"],"files":[],"path":""}}
    @Test
    public void testRoot() throws Exception {
//        final WebTarget target = client.target(URI.create(new URL(this.base, "api/folders").toExternalForm()));

        final String data = ClientBuilder.newClient()
                                         .target(UriBuilder.fromPath(this.base + "api/folders")
                                                           .build())
                                         .request(MediaType.APPLICATION_JSON)
                                         .get(String.class);

        System.out.println("data = " + data);
        assertThat(data, notNullValue());

        final JsonObject jsonObject = Json.createReader(new StringReader(data))
                                          .readObject();

        final String baseUri = jsonObject.getString("baseUri");
        assertThat(baseUri, endsWith("/api/folders"));
        final String browseUri = jsonObject.getString("browseUri");
        assertThat(browseUri, endsWith("/api/folders/"));

        final JsonObject folder = jsonObject.getJsonObject("folder");
        final JsonArray folders = folder.getJsonArray("folders");
        assertThat(folders.size(), is(2));


    }
}