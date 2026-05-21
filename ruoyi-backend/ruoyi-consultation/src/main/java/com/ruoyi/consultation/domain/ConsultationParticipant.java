package com.ruoyi.consultation.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问诊参与者对象 t_consultation_participant
 *
 * @author ruoyi
 */
public class ConsultationParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 参与者ID */
    private Long participantId;

    /** 关联问诊单ID */
    private Long consultationId;

    /** 用户ID */
    private Long userId;

    /** 用户类型（1=医生 2=病人 3=AI Agent） */
    private String userType;

    /** 加入房间时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    /** 离开房间时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveTime;

    /** LiveKit参与者标识 */
    private String livekitIdentity;

    /** 状态（0=已邀请 1=已加入 2=已离开） */
    private String status;

    /** 用户名称（关联查询） */
    private String userName;

    public Long getParticipantId()
    {
        return participantId;
    }

    public void setParticipantId(Long participantId)
    {
        this.participantId = participantId;
    }

    public Long getConsultationId()
    {
        return consultationId;
    }

    public void setConsultationId(Long consultationId)
    {
        this.consultationId = consultationId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public Date getJoinTime()
    {
        return joinTime;
    }

    public void setJoinTime(Date joinTime)
    {
        this.joinTime = joinTime;
    }

    public Date getLeaveTime()
    {
        return leaveTime;
    }

    public void setLeaveTime(Date leaveTime)
    {
        this.leaveTime = leaveTime;
    }

    public String getLivekitIdentity()
    {
        return livekitIdentity;
    }

    public void setLivekitIdentity(String livekitIdentity)
    {
        this.livekitIdentity = livekitIdentity;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
