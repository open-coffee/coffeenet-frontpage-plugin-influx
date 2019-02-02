package coffee.synyx.frontpage.plugin.influx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author  Ben Antony - antony@synyx.de
 */
class InfluxConfigTest {

    private InfluxConfig sut;

    @BeforeEach
    void setUp() {

        sut = new InfluxConfig();
    }


    @Test
    void isUrlPresent() {

        assertThat(sut.isUrlPresent()).isFalse();

        sut.setUrl("http://influx.com:8086");
        assertThat(sut.isUrlPresent()).isTrue();
    }


    @ParameterizedTest(
        name =
            "{index}: isWithAuthentication username set: {0} password set: {1} should result in isWithAuthentication() {2}"
    )
    @MethodSource("createAllPossibilities")
    void isWithAuthentication(boolean isUsernameSet, boolean isPasswordSet, boolean result) {

        if (isUsernameSet) {
            sut.setUsername("User");
        }

        if (isPasswordSet) {
            sut.setPassword("Password");
        }

        assertThat(sut.isWithAuthentication()).isEqualTo(result);
    }


    private static Stream<Arguments> createAllPossibilities() {

        return Stream.of(Arguments.of(false, false, false), Arguments.of(false, true, false),
                Arguments.of(true, false, false), Arguments.of(true, true, true));
    }
}
