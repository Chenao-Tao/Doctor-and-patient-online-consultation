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
}
