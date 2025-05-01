package dk.northtech.dassco_test_suite.metadata_model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalPublisherModel {
    private String name;
}
