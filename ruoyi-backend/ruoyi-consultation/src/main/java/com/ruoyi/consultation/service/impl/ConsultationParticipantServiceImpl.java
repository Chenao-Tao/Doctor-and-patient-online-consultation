package com.ruoyi.consultation.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.consultation.domain.ConsultationParticipant;
import com.ruoyi.consultation.mapper.ConsultationParticipantMapper;
import com.ruoyi.consultation.service.IConsultationParticipantService;

/**
 * 问诊参与者Service实现
 *
 * @author ruoyi
 */
@Service
public class ConsultationParticipantServiceImpl implements IConsultationParticipantService
{
    @Autowired
    private ConsultationParticipantMapper participantMapper;

    @Override
    public List<ConsultationParticipant> selectParticipantsByConsultationId(Long consultationId)
    {
        return participantMapper.selectParticipantsByConsultationId(consultationId);
    }

    @Override
    public ConsultationParticipant selectByConsultationAndUser(Long consultationId, Long userId)
    {
        return participantMapper.selectByConsultationAndUser(consultationId, userId);
    }

    @Override
    public int insertParticipant(ConsultationParticipant participant)
    {
        participant.setCreateTime(DateUtils.getNowDate());
        return participantMapper.insertParticipant(participant);
    }

    @Override
    public int updateParticipant(ConsultationParticipant participant)
    {
        return participantMapper.updateParticipant(participant);
    }

    @Override
    public int markParticipantJoined(Long consultationId, Long userId, String livekitIdentity)
    {
        return participantMapper.markParticipantJoined(consultationId, userId, livekitIdentity);
    }

    @Override
    public int markParticipantLeft(Long consultationId, Long userId)
    {
        return participantMapper.markParticipantLeft(consultationId, userId);
    }
}
