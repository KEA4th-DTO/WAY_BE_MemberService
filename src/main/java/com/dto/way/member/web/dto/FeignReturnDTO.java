package com.dto.way.member.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FeignReturnDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostCountDTO {

        private Integer dailyCount;
        private Integer historyCount;
    }
}
