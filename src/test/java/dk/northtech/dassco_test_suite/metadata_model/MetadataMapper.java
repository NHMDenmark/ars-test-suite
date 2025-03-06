package dk.northtech.dassco_test_suite.metadata_model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MetadataMapper {

    public Metadata derivative;
    public Metadata parent;

    // define paths for the json files
    private String path = "src/main/resources/non_static/";
    
    private final String parentFile = "model_parent_metadata.json";
    private final String derivativeFile = "model_derivative_metadata.json";

    public MetadataMapper(){

        System.out.println(derivativeFile);

        ObjectMapper objectMapper = new ObjectMapper();
        
        try{
            this.parent = objectMapper.readValue(new File(this.path + this.parentFile), Metadata.class); 
            this.derivative = objectMapper.readValue(new File(this.path + this.derivativeFile), Metadata.class);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    /*
    @Value("${parent.file}") 
    private final String parentFile;
    @Value("${derivative.file}")
    private final String derivativeFile;
     */

    /*
    public MetadataMapper(@Value("${parent.file}") String parentFile, @Value("${derivative.file}") String derivativeFile) {
        
        this.parentFile = parentFile;
        this.derivativeFile = derivativeFile;

        System.out.println(derivativeFile);

    }

    public void setMetadata(){
        ObjectMapper objectMapper = new ObjectMapper();
        
        try{
            this.parent = objectMapper.readValue(new File(this.path + this.parentFile), Metadata.class); 
            this.derivative = objectMapper.readValue(new File(this.path + this.derivativeFile), Metadata.class);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
     */
}
