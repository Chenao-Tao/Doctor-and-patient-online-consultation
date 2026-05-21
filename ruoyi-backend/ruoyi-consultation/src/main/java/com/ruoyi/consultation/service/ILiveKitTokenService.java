package com.ruoyi.consultation.service;

import java.util.Map;

/**
 * LiveKit Token生成Service接口
 *
 * @author ruoyi
 */
public interface ILiveKitTokenService
{
    /**
     * 生成LiveKit加入房间Token
     *
     * @param userId     用户ID
     * @param userName   用户名称
     * @param roomName   房间名
     * @param userType   用户类型（1=医生 2=病人 3=AI Agent）
     * @return Token字符串
     */
    public String generateToken(Long userId, String userName, String roomName, String userType);

    /**
     * 获取LiveKit连接信息（含Token）
     *
     * @param userId     用户ID
     * @param userName   用户名称
     * @param roomName   房间名
     * @param userType   用户类型
     * @return 连接信息Map
     */
    public Map<String, Object> getConnectionInfo(Long userId, String userName, String roomName, String userType);
}
