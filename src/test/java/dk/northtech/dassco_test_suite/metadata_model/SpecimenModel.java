package dk.northtech.dassco_test_suite.metadata_model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecimenModel{
    @Builder.Default
    private String institution = null;
    @Builder.Default
    private String collection = null;
    @Builder.Default
    private String barcode = null;
    @Builder.Default
    private String specimen_pid = "";
    @Builder.Default
    private ArrayList<String> preparation_types = new ArrayList<>();
    @Builder.Default
    private String asset_preparation_type = null;
}
