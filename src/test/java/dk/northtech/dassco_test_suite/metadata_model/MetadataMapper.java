package dk.northtech.dassco_test_suite.metadata_model;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MetadataMapper {

    public Metadata derivative;
    public Metadata parent;
    public String parentString;
    public String derivativeString;
    
    // define paths for the json files
    private String path = "src/main/resources/non_static/";
    
    //private final String parentFile = "v3.0.0_model_parent_metadata.json";
    //private final String derivativeFile = "v3.0.0_model_derivative_metadata.json";
    private final String parentFile = "v2_1_0_parent_metadata.json";
    private final String derivativeFile = "v2_1_0_derivative_metadata.json";

    public MetadataMapper(){

        ObjectMapper objectMapper = new ObjectMapper();
        
        try{
            this.parent = objectMapper.readValue(new File(this.path + this.parentFile), Metadata.class); 
            this.derivative = objectMapper.readValue(new File(this.path + this.derivativeFile), Metadata.class);
            this.parentString = objectMapper.writeValueAsString(this.parent);
            this.derivativeString = objectMapper.writeValueAsString(this.derivative);

        }
        catch(Exception e){
            System.out.println(e.toString());
        }

    }
}
}
