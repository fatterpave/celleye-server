package net.obstfelder.celleye;

import com.github.dirkraft.dropwizard.fileassets.FileAssetsBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.websockets.WebsocketBundle;
import net.obstfelder.celleye.services.ConnectionService;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Created by obsjoa on 10.02.2017.
 */
public class App extends Application<Config>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception
    {
        new App().run(args);
    }

    @Override
    public void run(Config config, Environment environment) throws Exception
    {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        final RestResource rest = new RestResource();
        final AppHealthCheck healthCheck = new AppHealthCheck();
        ConnectionService connectionService = new ConnectionService();
        environment.jersey().register(rest);
        LOGGER.info("Initialized Rest");
        environment.healthChecks().register("AppCheck",healthCheck);
        environment.jersey().register(healthCheck);
        LOGGER.info("Initialized WebSocket");
        environment.jersey().register(MultiPartFeature.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Bootstrap<Config> bootstrap)
    {
        bootstrap.addBundle(new AssetsBundle("/assets/","/celleye","index.html"));
        bootstrap.addBundle(new FileAssetsBundle("/app","/celleye/files"));
        bootstrap.addBundle(new WebsocketBundle(WebSocketResource.class));
    }

}
