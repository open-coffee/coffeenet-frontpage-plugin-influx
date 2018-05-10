package coffee.synyx.frontpage.plugin.influx;

import coffee.synyx.frontpage.plugin.api.ConfigurationDescription;
import coffee.synyx.frontpage.plugin.api.ConfigurationField;
import coffee.synyx.frontpage.plugin.api.ConfigurationFieldType;
import coffee.synyx.frontpage.plugin.api.ConfigurationInstance;
import coffee.synyx.frontpage.plugin.api.FrontpagePlugin;

import org.influxdb.InfluxDB;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static coffee.synyx.frontpage.plugin.api.ConfigurationFieldType.TEXT;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * @author  Ben Antony - antony@synyx.de
 */
@Component
public class InfluxPlugin implements FrontpagePlugin {

    private static final Logger LOGGER = getLogger(MethodHandles.lookup().lookupClass());

    private static final String METRIC_TITLE = "influx.title";
    private static final String QUERY = "influx.query";
    private static final String DATABASE = "influx.database";
    private static final String UNIT = "influx.unit";
    private static final String URL = "influx.url";

    private final InfluxConfig config;
    private final InfluxClientFactory connectionFactory;
    private final InfluxCommunicator communicator;

    @Autowired
    public InfluxPlugin(InfluxConfig config, InfluxClientFactory connectionFactory, InfluxCommunicator communicator) {

        LOGGER.info("Configuring InfluxPlugin with: {}", config);

        this.connectionFactory = connectionFactory;
        this.config = config;
        this.communicator = communicator;
    }

    @Override
    public String title(ConfigurationInstance configurationInstance) {

        return configurationInstance.get(METRIC_TITLE);
    }


    @Override
    public String content(ConfigurationInstance configurationInstance) {

        String query = configurationInstance.get(QUERY);
        String database = configurationInstance.get(DATABASE);
        String unit = configurationInstance.get(UNIT);

        String url = configurationInstance.get(URL);

        InfluxDB influxClient = connectionFactory.create(this.config, url);

        String value = communicator.getValue(influxClient, database, query, unit);

        return "<p style=\"font-size: 400%;text-align: center\">" + value + "</p>";
    }


    @Override
    public String id() {

        return "influx";
    }


    @Override
    public Optional<ConfigurationDescription> getConfigurationDescription() {

        List<ConfigurationField> configurationFields = new ArrayList<>();

        configurationFields.add(new ConfigurationField.Builder().id(METRIC_TITLE)
            .label("Title")
            .type(TEXT)
            .required(true)
            .build());

        configurationFields.add(new ConfigurationField.Builder().id(DATABASE)
            .label("Database")
            .type(TEXT)
            .required(true)
            .build());

        configurationFields.add(new ConfigurationField.Builder().id(QUERY)
            .label("Query")
            .type(TEXT)
            .required(true)
            .build());

        configurationFields.add(new ConfigurationField.Builder().id(UNIT)
            .label("Unit")
            .type(TEXT)
            .required(false)
            .build());

        if (!this.config.isUrlPresent()) {
            configurationFields.add(new ConfigurationField.Builder().id(URL)
                .label("Url")
                .type(ConfigurationFieldType.URL)
                .required(true)
                .build());
        }

        return Optional.of(() -> new HashSet<>(configurationFields));
    }
}
