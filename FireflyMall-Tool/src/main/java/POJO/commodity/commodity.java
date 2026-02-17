package POJO.commodity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class commodity {
    private Long id;
    private String name;
    private int price;
    private int stock;
    private String image;
    private String category;
    private String brand;
    private Map<String, Object> spec;
    private Integer sold;

    @JsonProperty("isAD")
    private boolean isAD;
    private Integer status;

    private String highlightString; // 高亮关键词
}
