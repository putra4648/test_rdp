package com.pradana.test_rdp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartshipDto {
    private String name;
    private String model;
}
