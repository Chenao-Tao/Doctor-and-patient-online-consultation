package com.ruoyi.consultation.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问诊单对象 t_consultation
 *
 * @author ruoyi
 */
public class Consultation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 问诊单ID */
    private Long consultationId;

    /** 病人用户ID */
    private Long patientId;

    /** 医生用户ID */
    private Long doctorId;

    /** LiveKit房间名 */
    private String roomName;

    /** 问诊状态（0=待接诊 1=进行中 2=已结束 3=已取消） */
    private String status;

    /** 问诊标题/主诉 */
    private String title;

    /** 病情描述 */
    private String description;

    /** 实际开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 实际结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 删除标志（0=正常 1=删除） */
    private String delFlag;

    /** 病人名称（关联查询） */
    private String patientName;

    /** 医生名称（关联查询） */
    private String doctorName;

    public Long getConsultationId()
    {
        return consultationId;
    }

    public void setConsultationId(Long consultationId)
    {
        this.consultationId = consultationId;
    }

    public Long getPatientId()
    {
        return patientId;
    }

    public void setPatientId(Long patientId)
    {
        this.patientId = patientId;
    }

    public Long getDoctorId()
    {
        return doctorId;
    }

    public void setDoctorId(Long doctorId)
    {
        this.doctorId = doctorId;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientName(String patientName)
    {
        this.patientName = patientName;
    }

    public String getDoctorName()
    {
        return doctorName;
    }

    public void setDoctorName(String doctorName)
    {
        this.doctorName = doctorName;
    }
}
