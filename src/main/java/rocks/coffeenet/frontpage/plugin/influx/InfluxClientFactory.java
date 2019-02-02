package rocks.coffeenet.frontpage.plugin.influx;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import org.springframework.stereotype.Component;


/**
 * @author  Ben Antony - antony@synyx.de
 */
@Component
public class InfluxClientFactory {

    InfluxDB create(InfluxConfig config, String url) {

        InfluxDB result;

        if (config.isUrlPresent()) {
            result = config.isWithAuthentication()
                ? InfluxDBFactory.connect(config.getUrl(), config.getUsername(), config.getPassword())
                : InfluxDBFactory.connect(config.getUrl());
        } else {
            result = InfluxDBFactory.connect(url);
        }

        return result;
    }
}
