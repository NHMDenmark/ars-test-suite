package dk.northtech.dassco_test_suite.metadata_model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueModel{
    private String name;
    @Builder.Default
    private String timestamp = null;
    private String description;
    private String note;
    @Builder.Default
    private boolean solved = false;
}