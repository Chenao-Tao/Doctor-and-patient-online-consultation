package com.ruoyi.consultation.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.consultation.config.LiveKitConfig;
import com.ruoyi.consultation.service.ILiveKitTokenService;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;

/**
 * LiveKit Token生成Service实现
 *
 * @author ruoyi
 */
@Service
public class LiveKitTokenServiceImpl implements ILiveKitTokenService
{
    @Autowired
    private LiveKitConfig liveKitConfig;

    @Override
    public String generateToken(Long userId, String userName, String roomName, String userType)
    {
        String identity = "user_" + userId;

        AccessToken token = new AccessToken(liveKitConfig.getApiKey(), liveKitConfig.getApiSecret());
        token.setIdentity(identity);
        token.setName(userName != null ? userName : identity);
        token.setMetadata(userType);

        // 添加房间权限：可以加入指定房间
        token.addGrants(new RoomJoin(true), new RoomName(roomName));

        // 设置过期时间（毫秒）
        token.setTtl(liveKitConfig.getTokenTtl() * 3600L * 1000L);

        return token.toJwt();
    }

    @Override
    public Map<String, Object> getConnectionInfo(Long userId, String userName, String roomName, String userType)
    {
        String token = generateToken(userId, userName, roomName, userType);
        Map<String, Object> info = new HashMap<>();
        info.put("token", token);
        info.put("wsUrl", liveKitConfig.getWsUrl());
        info.put("roomName", roomName);
        info.put("identity", "user_" + userId);
        info.put("userType", userType);
        return info;
    }
}
