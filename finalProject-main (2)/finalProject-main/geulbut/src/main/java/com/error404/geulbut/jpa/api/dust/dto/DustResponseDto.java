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
    public class DustResponseDto {

        @JsonProperty("body")
        private DustBodyDto body;

    }
