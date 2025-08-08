package dk.northtech.dassco_test_suite.metadata_model;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MetadataMapper {

    public Metadata derivative;
    public Metadata parent;
    public Metadata bridge;
    public Metadata mso;
    public UpdateMetadata updateBridge;
    public UpdateMetadata updateParent;
    public UpdateMetadata updateSecondBridge;
    public UpdateMetadata msoUpdate;

    public String parentString;
    public String derivativeString;
    public String bridgeString;
    public String updateBridgeString;
    public String updateParentString;
    public String updateSecondBridgeString;
    public String msoString;
    public String msoUpdateString;

    // define paths for the json files
    private String path = "src/main/resources/non_static/";

    private final String parentFile = "v3.0.3_model_parent_metadata.json";
    private final String derivativeFile = "v3.0.3_model_derivative_metadata.json";
    private final String updateParentFile = "v3.0.3_update_parent_metadata.json";
    private final String specifyBridgeFile = "v3.0.3_specify_bridge_metadata.json";
    private final String updateBridgeFile = "v3.0.3_update_specify_bridge_metadata.json";
    private final String updateSecondBridgeFile = "v3.0.3_update_second_bridge_metadata.json";
    private final String msoFile = "v3.0.3_mso_metadata.json";
    private final String msoUpdateFile = "v3.0.3_mso_update.json";

    public MetadataMapper() {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            
            this.parent = objectMapper.readValue(new File(this.path + this.parentFile), Metadata.class);
            this.derivative = objectMapper.readValue(new File(this.path + this.derivativeFile), Metadata.class);
            this.updateParent = objectMapper.readValue(new File(this.path + this.updateParentFile), UpdateMetadata.class);
            this.bridge = objectMapper.readValue(new File(this.path + this.specifyBridgeFile), Metadata.class);
            this.updateBridge = objectMapper.readValue(new File(this.path + this.updateBridgeFile), UpdateMetadata.class);
            this.updateSecondBridge = objectMapper.readValue(new File(this.path + this.updateSecondBridgeFile), UpdateMetadata.class);
            this.mso = objectMapper.readValue(new File(this.path + this.msoFile), Metadata.class);
            this.msoUpdate = objectMapper.readValue(new File(this.path + this.msoUpdateFile), UpdateMetadata.class);

            this.parentString = objectMapper.writeValueAsString(this.parent);
            this.derivativeString = objectMapper.writeValueAsString(this.derivative);
            this.updateParentString = objectMapper.writeValueAsString(this.updateParent);
            this.bridgeString = objectMapper.writeValueAsString(this.bridge);
            this.updateBridgeString = objectMapper.writeValueAsString(this.updateBridge);
            this.updateSecondBridgeString = objectMapper.writeValueAsString(this.updateSecondBridge);
            this.msoString = objectMapper.writeValueAsString(this.mso);
            this.msoUpdateString = objectMapper.writeValueAsString(this.msoUpdate);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
