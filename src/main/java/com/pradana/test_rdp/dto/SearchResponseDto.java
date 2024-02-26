package com.pradana.test_rdp.dto;

import java.util.List;

import lombok.Data;

@Data
public class SearchResponseDto {
    private String name;
    private String gender;
    private PlanetDto planet;
    private List<StartshipDto> startships;
}
