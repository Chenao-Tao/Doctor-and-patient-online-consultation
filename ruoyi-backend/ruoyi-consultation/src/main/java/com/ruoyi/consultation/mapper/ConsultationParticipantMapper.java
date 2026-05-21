package com.ruoyi.consultation.mapper;

import java.util.List;
import com.ruoyi.consultation.domain.ConsultationParticipant;

/**
 * 问诊参与者Mapper接口
 *
 * @author ruoyi
 */
public interface ConsultationParticipantMapper
{
    /**
     * 查询参与者
     */
    public ConsultationParticipant selectParticipantById(Long participantId);

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
     * 修改参与者
     */
    public int updateParticipant(ConsultationParticipant participant);

    /**
     * 删除参与者
     */
    public int deleteParticipantById(Long participantId);
}
