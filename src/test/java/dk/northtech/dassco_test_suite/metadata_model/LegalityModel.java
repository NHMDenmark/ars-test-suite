package dk.northtech.dassco_test_suite.metadata_model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LegalityModel{
    @Builder.Default
    private String copyright = null;
    @Builder.Default
    private String license = null;
    @Builder.Default
    private String credit = null;
}