package com.ruoyi.consultation.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.consultation.domain.Consultation;
import com.ruoyi.consultation.domain.ConsultationParticipant;
import com.ruoyi.consultation.mapper.ConsultationMapper;
import com.ruoyi.consultation.mapper.ConsultationParticipantMapper;
import com.ruoyi.consultation.service.IConsultationService;

/**
 * 问诊单Service实现
 *
 * @author ruoyi
 */
@Service
public class ConsultationServiceImpl implements IConsultationService
{
    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private ConsultationParticipantMapper participantMapper;

    @Override
    public Consultation selectConsultationById(Long consultationId)
    {
        return consultationMapper.selectConsultationById(consultationId);
    }

    @Override
    public List<Consultation> selectConsultationList(Consultation consultation)
    {
        return consultationMapper.selectConsultationList(consultation);
    }

    @Override
    public int insertConsultation(Consultation consultation)
    {
        consultation.setCreateTime(DateUtils.getNowDate());
        consultation.setStatus("0");
        consultation.setDelFlag("0");
        int rows = consultationMapper.insertConsultation(consultation);
        // 生成房间名(绑定问诊单)
        if (rows > 0 && (consultation.getRoomName() == null || consultation.getRoomName().isEmpty()))
        {
            Consultation update = new Consultation();
            update.setConsultationId(consultation.getConsultationId());
            update.setRoomName("consultation_" + consultation.getConsultationId());
            consultationMapper.updateConsultation(update);
            consultation.setRoomName(update.getRoomName());
        }
        // 同时插入病人参与者记录
        if (consultation.getPatientId() != null)
        {
            ConsultationParticipant participant = new ConsultationParticipant();
            participant.setConsultationId(consultation.getConsultationId());
            participant.setUserId(consultation.getPatientId());
            participant.setUserType("2");
            participant.setStatus("0");
            participant.setCreateTime(DateUtils.getNowDate());
            participantMapper.insertParticipant(participant);
        }
        // 若已指定医生, 同时插入医生参与者记录
        if (consultation.getDoctorId() != null)
        {
            ConsultationParticipant existing = participantMapper.selectByConsultationAndUser(
                    consultation.getConsultationId(), consultation.getDoctorId());
            if (existing == null)
            {
                ConsultationParticipant participant = new ConsultationParticipant();
                participant.setConsultationId(consultation.getConsultationId());
                participant.setUserId(consultation.getDoctorId());
                participant.setUserType("1");
                participant.setStatus("0");
                participant.setCreateTime(DateUtils.getNowDate());
                participantMapper.insertParticipant(participant);
            }
        }
        return rows;
    }

    @Override
    public int updateConsultation(Consultation consultation)
    {
        consultation.setUpdateTime(DateUtils.getNowDate());
        return consultationMapper.updateConsultation(consultation);
    }

    @Override
    public int deleteConsultationByIds(Long[] consultationIds)
    {
        return consultationMapper.deleteConsultationByIds(consultationIds);
    }

    @Override
    public int startConsultation(Long consultationId, Long doctorId)
    {
        Consultation consultation = consultationMapper.selectConsultationById(consultationId);
        if (consultation == null || !"0".equals(consultation.getStatus()))
        {
            return 0;
        }
        if (consultation.getDoctorId() != null && !doctorId.equals(consultation.getDoctorId()))
        {
            return 0;
        }
        // 更新问诊单状态
        Consultation update = new Consultation();
        update.setConsultationId(consultationId);
        update.setDoctorId(doctorId);
        update.setStatus("1");
        update.setStartTime(DateUtils.getNowDate());
        update.setUpdateTime(DateUtils.getNowDate());
        int rows = consultationMapper.updateConsultation(update);
        // 插入医生参与者记录
        ConsultationParticipant existing = participantMapper.selectByConsultationAndUser(consultationId, doctorId);
        if (existing == null)
        {
            ConsultationParticipant participant = new ConsultationParticipant();
            participant.setConsultationId(consultationId);
            participant.setUserId(doctorId);
            participant.setUserType("1");
            participant.setStatus("0");
            participant.setCreateTime(DateUtils.getNowDate());
            participantMapper.insertParticipant(participant);
        }
        return rows;
    }

    @Override
    public int endConsultation(Long consultationId)
    {
        Consultation consultation = consultationMapper.selectConsultationById(consultationId);
        if (consultation == null || !"1".equals(consultation.getStatus()))
        {
            return 0;
        }
        Consultation update = new Consultation();
        update.setConsultationId(consultationId);
        update.setStatus("2");
        update.setEndTime(DateUtils.getNowDate());
        update.setUpdateTime(DateUtils.getNowDate());
        return consultationMapper.updateConsultation(update);
    }

    @Override
    public int cancelConsultation(Long consultationId)
    {
        Consultation consultation = consultationMapper.selectConsultationById(consultationId);
        if (consultation == null || !"0".equals(consultation.getStatus()))
        {
            return 0;
        }
        Consultation update = new Consultation();
        update.setConsultationId(consultationId);
        update.setStatus("3");
        update.setUpdateTime(DateUtils.getNowDate());
        return consultationMapper.updateConsultation(update);
    }

    @Override
    public String resolveUserRole(Long consultationId, Long userId)
    {
        Consultation consultation = consultationMapper.selectConsultationById(consultationId);
        if (consultation == null)
        {
            return null;
        }
        if (userId.equals(consultation.getDoctorId()))
        {
            return "1"; // 医生
        }
        if (userId.equals(consultation.getPatientId()))
        {
            return "2"; // 病人
        }
        return null;
    }
}
