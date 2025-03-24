package dk.northtech.dassco_test_suite.metadata_model;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MetadataMapper {

    public Metadata derivative;
    public Metadata parent;
    public UpdateMetadata updateParent;
    public String parentString;
    public String derivativeString;
    public String updateParentString;
    
    // define paths for the json files
    private String path = "src/main/resources/non_static/";
    
    //private final String parentFile = "v3.0.2_model_parent_metadata.json";
    //private final String derivativeFile = "v3.0.2_model_derivative_metadata.json";
    //private final String updateParentFile = "v3.0.2_update_parent_metadata.json";
    private final String parentFile = "v2_1_0_parent_metadata.json";
    private final String derivativeFile = "v2_1_0_derivative_metadata.json";
    private final String updateParentFile = "v2_1_0_update_parent_metadata.json";

    public MetadataMapper(){

        ObjectMapper objectMapper = new ObjectMapper();
        
        try{
            this.parent = objectMapper.readValue(new File(this.path + this.parentFile), Metadata.class); 
            this.derivative = objectMapper.readValue(new File(this.path + this.derivativeFile), Metadata.class);
            this.updateParent = objectMapper.readValue(new File(this.path + this.updateParentFile), UpdateMetadata.class);
            this.parentString = objectMapper.writeValueAsString(this.parent);
            this.derivativeString = objectMapper.writeValueAsString(this.derivative);
            this.updateParentString = objectMapper.writeValueAsString(this.updateParent);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
}

