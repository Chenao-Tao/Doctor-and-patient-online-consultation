package com.ruoyi.consultation.service;

import java.util.List;
import com.ruoyi.consultation.domain.ConsultationParticipant;

/**
 * 问诊参与者Service接口
 *
 * @author ruoyi
 */
public interface IConsultationParticipantService
{
    /**
     * 查询问诊的参与者列表
     */
    public List<ConsultationParticipant> selectParticipantsByConsultationId(Long consultationId);

    /**
     * 查询用户在某问诊中的参与者记录
     */
    public ConsultationParticipant selectByConsultationAndUser(Long consultationId, Long userId);

    /**
     * 新增参与者
     */
    public int insertParticipant(ConsultationParticipant participant);

    /**
     * 修改参与者（如标记加入/离开时间）
     */
    public int updateParticipant(ConsultationParticipant participant);

    /**
     * 标记当前参与者已进入LiveKit房间
     */
    public int markParticipantJoined(Long consultationId, Long userId, String livekitIdentity);

    /**
     * 标记当前参与者已离开LiveKit房间
     */
    public int markParticipantLeft(Long consultationId, Long userId);
}
