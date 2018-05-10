package coffee.synyx.frontpage.plugin.influx;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


/**
 * @author  Ben Antony - antony@synyx.de
 */
@Component
class InfluxCommunicator {

    String getValue(InfluxDB client, String database, String queryString, String unit) {

        Query query = new Query(queryString, database);

        try {
            QueryResult queryResult = client.query(query);

            return parseQueryResult(queryResult, unit);
        } catch (InfluxDBException e) {
            return "Something went wrong with your query... \uD83D\uDE2D";
        }
    }


    private String parseQueryResult(QueryResult queryResult, String unit) {

        return extractResults(queryResult).max(Comparator.comparing(InfluxResult::getDateTime))
            .map(InfluxResult::getValue)
            .map(v -> v + " " + unit)
            .orElse("nothing found");
    }


    private Stream<InfluxResult> extractResults(QueryResult queryResult) {

        return queryResult.getResults()
            .stream()
            .map(QueryResult.Result::getSeries)
            .flatMap(List::stream)
            .map(QueryResult.Series::getValues)
            .flatMap(List::stream)
            .map(InfluxResult::new);
    }

    private class InfluxResult {

        private final LocalDateTime dateTime;
        private final String value;

        InfluxResult(List<Object> objects) {

            this.dateTime = LocalDateTime.parse(String.valueOf(objects.get(0)));
            this.value = String.valueOf(objects.get(1));
        }

        LocalDateTime getDateTime() {

            return dateTime;
        }


        String getValue() {

            return value;
        }
    }
}
