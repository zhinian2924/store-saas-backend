package com.example.storesaas.mini.service;

import com.example.storesaas.common.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WechatClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public WechatClient(ObjectMapper objectMapper) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(8_000);
        this.restClient = RestClient.builder().baseUrl("https://api.weixin.qq.com")
                .requestFactory(requestFactory).build();
        this.objectMapper = objectMapper;
    }

    /**
     * 通过微信授权登录的code，获取session信息
     * @param appId 微信小程序appId
     * @param appSecret 微信小程序appSecret
     * @param code 授权登录的code
     * @return session信息
     */
    public WechatSession exchange(String appId, String appSecret, String code) {
        try {
            String responseBody = restClient.get()
                    .uri(uri -> uri.path("/sns/jscode2session")
                            .queryParam("appid", appId)
                            .queryParam("secret", appSecret)
                            .queryParam("js_code", code)
                            .queryParam("grant_type", "authorization_code")
                            .build())
                    .retrieve()
                    .body(String.class);
            return parseResponse(responseBody);
        } catch (BusinessException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw serviceUnavailable();
        }
    }

    WechatSession parseResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            throw serviceUnavailable();
        }
        try {
            WechatSession response = objectMapper.readValue(responseBody, WechatSession.class);
            if (response == null) {
                throw serviceUnavailable();
            }
            if (response.openid() == null || response.openid().isBlank()) {
                throw new BusinessException("微信登录凭证无效，请重试");
            }
            return response;
        } catch (JsonProcessingException ex) {
            throw serviceUnavailable();
        }
    }

    private BusinessException serviceUnavailable() {
        return new BusinessException("微信登录服务暂时不可用，请重试");
    }

    public record WechatSession(
            String openid,
            @JsonProperty("session_key") String sessionKey,
            String unionid,
            Integer errcode,
            String errmsg
    ) {
    }
}
