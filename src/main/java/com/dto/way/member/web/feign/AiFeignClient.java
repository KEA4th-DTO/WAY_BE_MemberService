package com.dto.way.member.web.feign;

import com.dto.way.member.global.config.FeignClientConfig;
import com.dto.way.member.web.dto.UserTagRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "way-be-ai", url = "http://210.109.55.124/ai-service", configuration = FeignClientConfig.class)
public interface AiFeignClient {

    @PostMapping("/user_tag")
    List<String> getUserTags(@RequestBody UserTagRequestDTO userTagRequestDTO);
}