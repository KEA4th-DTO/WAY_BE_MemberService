package com.dto.way.member.web.feign;

import com.dto.way.member.global.config.FeignClientConfig;
import com.dto.way.member.web.dto.UserTagRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "way-be-ai", url = "http://210.109.55.124/ai-service", configuration = FeignClientConfig.class)
public interface AiFeignClient {

    @PostMapping(value = "/user_tag", consumes = "multipart/form-data")
    List<String> getUserTags(@RequestPart("user_id") Long user_id,
                             @RequestPart("image_url") String image_url,
                             @RequestPart("text_url") String text_url);
}