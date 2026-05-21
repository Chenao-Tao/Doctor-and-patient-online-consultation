package com.ruoyi.consultation.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问诊消息记录对象 t_consultation_message
 *
 * @author ruoyi
 */
public class ConsultationMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long messageId;

    /** 关联问诊单ID */
    private Long consultationId;

    /** 发送者用户ID */
    private Long senderId;

    /** 发送者类型（1=医生 2=病人 3=AI Agent） */
    private String senderType;

    /** 消息类型（1=文本 2=图片 3=系统通知） */
    private String messageType;

    /** 消息内容 */
    private String content;

    /** 发送者名称（关联查询） */
    private String senderName;

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public Long getConsultationId()
    {
        return consultationId;
    }

    public void setConsultationId(Long consultationId)
    {
        this.consultationId = consultationId;
    }

    public Long getSenderId()
    {
        return senderId;
    }

    public void setSenderId(Long senderId)
    {
        this.senderId = senderId;
    }

    public String getSenderType()
    {
        return senderType;
    }

    public void setSenderType(String senderType)
    {
        this.senderType = senderType;
    }

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }
}
