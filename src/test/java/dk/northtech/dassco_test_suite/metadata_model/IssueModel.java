package dk.northtech.dassco_test_suite.metadata_model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueModel {
    private String category;
    @Builder.Default
    private String name = null;
    @Builder.Default
    private String timestamp = null;
    @Builder.Default
    private String status = null;
    @Builder.Default
    private String description = null;
    @Builder.Default
    private String notes = null;
    @Builder.Default
    private boolean solved = false;
}