package com.error404.geulbut.jpa.api.dust.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DustDto {
    @JsonProperty("dataTime")
    private String dataTime;

    @JsonProperty("informCode")
    private String informCode;

    @JsonProperty("informOverall")  // 수정
    private String informOverall;

    @JsonProperty("informCause")
    private String informCause;

    @JsonProperty("informGrade")
    private String informGrade;

    @JsonProperty("actionKnack")
    private String actionKnack;

    @JsonProperty("imageUrl1")
    private String imageUrl1;

    @JsonProperty("imageUrl2")
    private String imageUrl2;

    @JsonProperty("imageUrl3")
    private String imageUrl3;

    @JsonProperty("imageUrl4")
    private String imageUrl4;

    @JsonProperty("imageUrl5")
    private String imageUrl5;

    @JsonProperty("imageUrl6")
    private String imageUrl6;

    @JsonProperty("imageUrl7")
    private String imageUrl7;

    @JsonProperty("imageUrl8")
    private String imageUrl8;

    @JsonProperty("imageUrl9")
    private String imageUrl9;

    @JsonProperty("informData")
    private String informData;
}
