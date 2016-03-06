package nl.ivonet.service.config;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

public class Producers {
    private Properties properties;

    @Produces
    public Logger loggerExposer(final InjectionPoint ip) {
        return getLogger(ip.getMember()
                           .getDeclaringClass()
                           .getName());
    }


    @Property
    @Produces
    public String produceString(final InjectionPoint ip) {
        final String key = ip.getAnnotated()
                             .isAnnotationPresent(Property.class) ? ip.getAnnotated()
                                                                      .getAnnotation(Property.class)
                                                                      .value() : ip.getMember()
                                                                                   .getName();
        return this.properties.getProperty(key);
    }

    @Property
    @Produces
    public int produceInt(final InjectionPoint ip) {
        final String key = ip.getAnnotated()
                             .isAnnotationPresent(Property.class) ? ip.getAnnotated()
                                                                      .getAnnotation(Property.class)
                                                                      .value() : ip.getMember()
                                                                                   .getName();
        return Integer.valueOf(this.properties.getProperty(key));
    }

    @Property
    @Produces
    public boolean produceBoolean(final InjectionPoint ip) {
        final String temp = this.properties.getProperty(ip.getMember()
                                                          .getName());
        return Boolean.valueOf(temp);
    }

    @PostConstruct
    public void init() {
        this.properties = new Properties();
        final InputStream stream = Producers.class.getResourceAsStream("/application.properties");
        if (stream == null) {
            throw new RuntimeException("No properties!!!");
        }
        try {
            this.properties.load(stream);
        } catch (final IOException e) {
            throw new RuntimeException("Configuration could not be loaded!");
        }
    }

}
