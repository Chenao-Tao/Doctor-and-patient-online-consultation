package com.ruoyi.consultation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * LiveKit配置属性
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "livekit")
public class LiveKitConfig
{
    /** LiveKit API Key */
    private String apiKey;

    /** LiveKit API Secret */
    private String apiSecret;

    /** LiveKit WebSocket URL */
    private String wsUrl;

    /** Token有效期（小时） */
    private int tokenTtl = 6;

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getApiSecret()
    {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret)
    {
        this.apiSecret = apiSecret;
    }

    public String getWsUrl()
    {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl)
    {
        this.wsUrl = wsUrl;
    }

    public int getTokenTtl()
    {
        return tokenTtl;
    }

    public void setTokenTtl(int tokenTtl)
    {
        this.tokenTtl = tokenTtl;
    }
}
