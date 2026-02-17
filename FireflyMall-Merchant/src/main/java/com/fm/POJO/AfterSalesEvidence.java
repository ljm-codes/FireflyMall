package com.fm.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AfterSalesEvidence {

    private Long id;
    private Long apply_id;
    private Integer evidence_type;
    private String file_url;
    private String file_name;
    private LocalDateTime  create_time;

}
