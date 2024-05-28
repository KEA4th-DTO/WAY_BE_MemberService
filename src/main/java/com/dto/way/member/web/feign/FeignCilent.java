package com.dto.way.member.web.feign;

import com.dto.way.member.global.config.FeignClientConfig;
import com.dto.way.member.web.dto.FeignReturnDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "post-service", url = "${config.feign.post-url}", configuration = FeignClientConfig.class)
public interface FeignCilent {

    @GetMapping("/posts/count")
    FeignReturnDTO.PostCountDTO getPostsCount(@RequestParam Long memberId);
}
