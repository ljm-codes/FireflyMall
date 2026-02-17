package com.fm.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class user {
    private String info;
    private String email;
    private Name name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Name{
        private String firstName;
        private String lastName;
    }
}
