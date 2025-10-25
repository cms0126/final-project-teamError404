package com.error404.geulbut.jpa.api.dust.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DustBodyDto {

    @JsonProperty
    private int numOfRows;

    @JsonProperty
    private int pageNo;

    @JsonProperty
    private int totalCount;

    @JsonProperty("items")
    private List<DustDto> items;


}
