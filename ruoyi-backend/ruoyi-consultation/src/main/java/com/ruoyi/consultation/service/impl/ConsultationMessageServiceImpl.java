package com.ruoyi.consultation.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.consultation.domain.ConsultationMessage;
import com.ruoyi.consultation.mapper.ConsultationMessageMapper;
import com.ruoyi.consultation.service.IConsultationMessageService;

/**
 * 问诊消息记录Service实现
 *
 * @author ruoyi
 */
@Service
public class ConsultationMessageServiceImpl implements IConsultationMessageService
{
    @Autowired
    private ConsultationMessageMapper messageMapper;

    @Override
    public List<ConsultationMessage> selectMessagesByConsultationId(Long consultationId)
    {
        return messageMapper.selectMessagesByConsultationId(consultationId);
    }

    @Override
    public int insertMessage(ConsultationMessage message)
    {
        message.setCreateTime(DateUtils.getNowDate());
        return messageMapper.insertMessage(message);
    }
}
