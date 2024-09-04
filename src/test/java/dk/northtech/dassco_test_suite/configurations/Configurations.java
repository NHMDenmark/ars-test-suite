package dk.northtech.dassco_test_suite.configurations;

import dk.northtech.dassco_test_suite.conditions.Conditions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class Configurations {

    // Configures the Environmental variables to be accessible in the Conditions class:

    private final Environment env;

    @Autowired
    public Configurations(Environment env){
        this.env = env;
    }

    @PostConstruct
    public void init(){
        Conditions.init(env);
    }

}
