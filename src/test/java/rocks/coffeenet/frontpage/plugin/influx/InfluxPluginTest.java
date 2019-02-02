package rocks.coffeenet.frontpage.plugin.influx;

import org.influxdb.InfluxDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.coffeenet.frontpage.plugin.api.ConfigurationDescription;
import rocks.coffeenet.frontpage.plugin.api.ConfigurationInstance;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * @author  Ben Antony - antony@synyx.de
 */
@ExtendWith(MockitoExtension.class)
class InfluxPluginTest {

    private InfluxPlugin sut;

    @Mock
    private InfluxClientFactory connectionFactoryMock;
    @Mock
    private InfluxCommunicator communicatorMock;

    private InfluxConfig config;

    @BeforeEach
    void setUp() {

        this.config = new InfluxConfig();
    }


    @Test
    void title() {

        this.config.setUrl("http://influx.com:8086");

        sut = new InfluxPlugin(this.config, connectionFactoryMock, communicatorMock);

        ConfigurationInstance configurationInstance = mock(ConfigurationInstance.class);

        doReturn("fancy Title").when(configurationInstance).get("influx.title");

        assertThat(sut.title(configurationInstance)).isEqualTo("fancy Title");
    }


    @Test
    void id() {

        this.config.setUrl("http://influx.com:8086");

        sut = new InfluxPlugin(this.config, connectionFactoryMock, communicatorMock);

        assertThat(sut.id()).isEqualTo("influx");
    }


    @Test
    void getConfigurationDescription() {

        this.config.setUrl("http://influx.com:8086");

        sut = new InfluxPlugin(this.config, connectionFactoryMock, communicatorMock);

        Optional<ConfigurationDescription> result = sut.getConfigurationDescription();

        assertThat(result).isPresent();
        assertThat(result.get().getConfigurations()).hasSize(4);
    }


    @Test
    void getConfigurationDescriptionWithoutUrl() {

        sut = new InfluxPlugin(this.config, connectionFactoryMock, communicatorMock);

        Optional<ConfigurationDescription> result = sut.getConfigurationDescription();

        assertThat(result).isPresent();
        assertThat(result.get().getConfigurations()).hasSize(5);
    }


    @Test
    void content() {

        sut = new InfluxPlugin(this.config, connectionFactoryMock, communicatorMock);

        ConfigurationInstance configurationInstance = mock(ConfigurationInstance.class);
        InfluxDB influxClientMock = mock(InfluxDB.class);

        doReturn("http://influx.com:8086").when(configurationInstance).get("influx.url");
        doReturn("SELECT Query").when(configurationInstance).get("influx.query");
        doReturn("iot").when(configurationInstance).get("influx.database");
        doReturn("°C").when(configurationInstance).get("influx.unit");

        doReturn(influxClientMock).when(connectionFactoryMock).create(config, "http://influx.com:8086");
        doReturn("value").when(communicatorMock).getValue(influxClientMock, "iot", "SELECT Query", "°C");

        assertThat(sut.content(configurationInstance)).isEqualTo(
            "<p style=\"font-size: 400%;text-align: center\">value</p>");
    }
}
