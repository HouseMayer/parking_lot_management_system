package com.example.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInfoVO implements Serializable {

    private String avatar;
    private String introduction;
    private String name;
    private List<String> roles;

}
