package coffee.synyx.frontpage.plugin.influx;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Configuration;

import org.springframework.util.StringUtils;


/**
 * @author  Ben Antony - antony@synyx.de
 */
@Configuration
@ConfigurationProperties("plugins.influx")
public class InfluxConfig {

    private String url;
    private String username;
    private String password;

    public String getUrl() {

        return url;
    }


    public void setUrl(String url) {

        this.url = url;
    }


    public String getUsername() {

        return username;
    }


    public void setUsername(String username) {

        this.username = username;
    }


    public String getPassword() {

        return password;
    }


    public void setPassword(String password) {

        this.password = password;
    }


    public boolean isWithAuthentication() {

        return !StringUtils.isEmpty(this.username) && !StringUtils.isEmpty(this.password);
    }


    public boolean isUrlPresent() {

        return !StringUtils.isEmpty(this.url);
    }


    @Override
    public String toString() {

        return "InfluxConfig{"
            + "url='" + url + '\''
            + ", username='" + username + '\''
            + ", password='" + password + '\'' + '}';
    }
}
