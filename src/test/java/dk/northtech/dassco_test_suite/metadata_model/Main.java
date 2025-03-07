package dk.northtech.dassco_test_suite.metadata_model;

import dk.northtech.dassco_test_suite.metadata_model.MetadataMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


// tester class for metadata test changes
public class Main {

    public static void main(String[] args){
        
        MetadataMapper mdata = new MetadataMapper();
        
        // check some specific value from metadata
        String value = mdata.parent.getAsset_guid();
        System.out.println(value);
       
        // check and print entire metadata as a json
        ObjectMapper mapper = new ObjectMapper();
        String body;
        try {
            body = mapper.writeValueAsString(mdata.parent);
        } 
        catch (JsonProcessingException e) {
            e.printStackTrace();
            body = "{}";
        }        
        System.out.println(body);
    }
}

/*package dk.northtech.dassco_test_suite.metadata_model;

import org.springframework.stereotype.Service;

import dk.northtech.dassco_test_suite.metadata_model.MetadataMapper;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class Run{

    private MetadataMapper metadataMapper;

    @Autowired
    public Run(MetadataMapper metadataMapper) {
        this.metadataMapper = metadataMapper;
    }

    public void something(){

        metadataMapper.setMetadata();

    }
}
    */
