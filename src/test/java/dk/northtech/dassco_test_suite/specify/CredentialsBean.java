package dk.northtech.dassco_test_suite.specify;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

// set the variables in a application-local.properties file in the root directory
@Configuration
@PropertySource("file:.\\application-local.properties")
public class CredentialsBean {

    @Value("${specify.id}")
    private String specifyId;
    @Value("${specify.secret}")
    private String specifySecret;
    @Value("${specify.url}")
    private String specifyUrl;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public String beanSpecifyId() {
        return this.specifyId;
    }
    @Bean
    public String beanSpecifySecret() {
        return this.specifySecret;
    }
    @Bean
    public String beanSpecifyUrl() {
        return this.specifyUrl;
    }
}