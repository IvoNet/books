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

package nl.ivonet.integration_tests;

import nl.ivonet.service.config.BootStrap;
import nl.ivonet.service.directory.Directory;
import nl.ivonet.service.service.FolderService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Ivo Woltring
 */
@RunWith(Arquillian.class)
public class FolderServiceIT {
    private WebTarget target;
    @ArquillianResource
    private URL base;

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                         .addPackage(BootStrap.class.getPackage())
                         .addPackage(Directory.class.getPackage())
                         .addPackage(FolderService.class.getPackage())
                         .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void setUp() throws MalformedURLException {
        final Client client = ClientBuilder.newClient();
        this.target = client.target(URI.create(new URL(this.base, "api/folder").toExternalForm()));
    }

    @Test
    public void findPerson() {
        final String message = this.target.path("{name}").resolveTemplate("name", "Ivo").request(MediaType.TEXT_PLAIN).get(String.class);

        assertThat(message, notNullValue());
        assertThat(message, is("Hello Ivo"));
    }
}
