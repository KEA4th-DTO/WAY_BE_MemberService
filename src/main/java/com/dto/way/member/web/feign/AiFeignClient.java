package com.dto.way.member.web.feign;

import com.dto.way.member.global.config.FeignClientConfig;
import com.dto.way.member.web.dto.UserTagRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "way-be-ai", url = "${config.feign.ai-url}", configuration = FeignClientConfig.class)
public interface AiFeignClient {

    @PostMapping(value = "/user_tag", consumes = "multipart/form-data")
    List<String> getUserTags(@RequestPart("user_id") Long user_id,
                             @RequestPart("image_url") String image_url,
                             @RequestPart("text_url") String text_url);

    // Long user_id 를 파라미터로 넘김
    @PostMapping(value = "/recommendation", consumes = "multipart/form-data")
    List<Long> getRecommendMember(@RequestPart("user_id") Long user_id);
}