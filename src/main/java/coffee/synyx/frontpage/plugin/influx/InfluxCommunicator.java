package coffee.synyx.frontpage.plugin.influx;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import org.slf4j.Logger;

import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;


/**
 * @author  Ben Antony - antony@synyx.de
 */
@Component
class InfluxCommunicator {

    private static final Logger LOG = getLogger(MethodHandles.lookup().lookupClass());

    String getValue(InfluxDB client, String database, String queryString, String unit) {

        Query query = new Query(queryString, database);

        try {
            QueryResult queryResult = client.query(query);

            return parseQueryResult(queryResult, unit);
        } catch (InfluxDBException e) {
            LOG.error("Something went wrong with a query", e.getMessage(), e);

            return "Something went wrong with your query... \uD83D\uDE2D";
        } catch (DateTimeException e) {
            LOG.error("Error parsing timestamp: {}", e.getMessage(), e);

            return "Something went wrong while parsing a timestamp \uD83E\uDDD0";
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

            this.dateTime = LocalDateTime.parse(String.valueOf(objects.get(0)), ISO_DATE_TIME);
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
