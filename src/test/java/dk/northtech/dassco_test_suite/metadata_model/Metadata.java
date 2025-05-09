package dk.northtech.dassco_test_suite.metadata_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

 @Data
 @AllArgsConstructor
 @NoArgsConstructor
 @Builder
 public class Metadata {
 // for metadata version 3.0.2
    private String asset_created_by;
    private String asset_deleted_by;
    private String asset_guid;
    private String asset_pid;
    private String asset_subject;
    private String asset_updated_by;
    private boolean audited;
    private String audited_by;
 
    @Builder.Default
    private ArrayList<String> barcode = new ArrayList<>();
    private String camera_setting_control;
    private String collection;

    @Builder.Default
    private ArrayList<String> complete_digitiser_list = new ArrayList<>();
    private String date_asset_created_ars;
    private String date_asset_deleted_ars;
    private String date_asset_finalised;
    private String date_asset_taken;
    private String date_asset_updated_ars;
    private String date_audited;
    private String date_metadata_created_ars;
    private String date_metadata_ingested;
    private String date_metadata_updated_ars;
    private String date_pushed_to_specify;
    private String digitiser;
 
    @Builder.Default
    private ArrayList<ExternalPublisherModel> external_publishers = new ArrayList<>();
 
    @Builder.Default
    private ArrayList<String> file_format = new ArrayList<>();
 
    @Builder.Default
    private ArrayList<String> funding = new ArrayList<>();
    private String institution;
 
    @Builder.Default
    private ArrayList<IssueModel> issues = new ArrayList<>();
 
    @Builder.Default
    private LegalityModel legality = new LegalityModel();
    private boolean make_public;
    private String metadata_created_by;
    private String metadata_source;
    private String metadata_updated_by;
    private String metadata_version;
    private String mos_id;
    private boolean multi_specimen;
  
    @Builder.Default
    private ArrayList<String> parent_guids = new ArrayList<>();
    private String payload_type;
    private String pipeline_name;
    private ArrayList<String> preparation_type;
    private boolean push_to_specify;
 
    @Builder.Default
    private ArrayList<String> restricted_access = new ArrayList<>();
    private String specify_attachment_remarks;
    private String specify_attachment_title;
    private String specimen_pid;
  
    @Builder.Default
    private String status = "WORKING_COPY";
 
    @Builder.Default
    private Map<String, String> tags = new HashMap<>();
    private String workstation_name;
    }
 