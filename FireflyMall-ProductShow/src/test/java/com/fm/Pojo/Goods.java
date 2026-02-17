package com.fm.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {
    private Long id;
    private String name;
    private int price;
    private int stock;
    private String image;
    private String category;
    private String brand;
    private String spec;
    private Integer sold;
    private Integer isAD;
    private Integer status;
}
