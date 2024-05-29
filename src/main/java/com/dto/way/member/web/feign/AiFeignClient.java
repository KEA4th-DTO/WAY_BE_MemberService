package com.dto.way.member.web.feign;

import com.dto.way.member.global.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "way-be-ai", url = "http://10.109.236.96:50009/", configuration = FeignClientConfig.class)
public interface AiFeignClient {

    @PostMapping("/user_tag")
    List<String> getUserTags(@RequestParam("user_id") Long userId,
                             @RequestParam("image_url") String imageUrl,
                             @RequestParam("text_url") String textUrl);
}