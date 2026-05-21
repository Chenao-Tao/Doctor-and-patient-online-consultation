package com.ruoyi.consultation.mapper;

import java.util.List;
import com.ruoyi.consultation.domain.ConsultationMessage;

/**
 * 问诊消息记录Mapper接口
 *
 * @author ruoyi
 */
public interface ConsultationMessageMapper
{
    /**
     * 查询问诊消息列表
     */
    public List<ConsultationMessage> selectMessagesByConsultationId(Long consultationId);

    /**
     * 新增消息
     */
    public int insertMessage(ConsultationMessage message);
}
