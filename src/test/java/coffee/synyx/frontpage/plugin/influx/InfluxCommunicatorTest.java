package coffee.synyx.frontpage.plugin.influx;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;


/**
 * @author  Ben Antony - antony@synyx.de
 */
@ExtendWith(MockitoExtension.class)
class InfluxCommunicatorTest {

    @Mock
    private InfluxDB clientMock;
    private InfluxCommunicator sut;

    @BeforeEach
    void setUp() {

        sut = new InfluxCommunicator();
    }


    @Test
    void getValue() {

        Query query = new Query("SELECT * FROM", "iot");
        doReturn(influxResult()).when(clientMock).query(query);

        String result = sut.getValue(clientMock, "iot", "SELECT * FROM", "°C");

        assertThat(result).isEqualTo("24.5 °C");
    }


    @Test
    void getValueInfluxException() {

        Query query = new Query("SELECT * FROM", "iot");
        doThrow(InfluxDBException.class).when(clientMock).query(query);

        String result = sut.getValue(clientMock, "iot", "SELECT * FROM", null);

        assertThat(result).isEqualTo("Something went wrong with your query... \uD83D\uDE2D");
    }


    private QueryResult influxResult() {

        List<Object> values1 = asList("2018-05-08T18:15:30", 23.5);
        List<Object> values2 = asList("2018-05-07T18:15:30", 22.5);
        List<Object> values3 = asList("2018-05-10T18:15:30", 24.5);

        QueryResult.Series series = new QueryResult.Series();
        series.setValues(asList(values1, values2, values3));

        QueryResult.Result result1 = new QueryResult.Result();
        result1.setSeries(singletonList(series));

        QueryResult result = new QueryResult();
        result.setResults(singletonList(result1));

        return result;
    }
}
