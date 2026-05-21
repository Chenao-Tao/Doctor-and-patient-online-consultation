package com.ruoyi.consultation.service;

import java.util.List;
import com.ruoyi.consultation.domain.Consultation;

/**
 * 问诊单Service接口
 *
 * @author ruoyi
 */
public interface IConsultationService
{
    /**
     * 查询问诊单
     */
    public Consultation selectConsultationById(Long consultationId);

    /**
     * 查询问诊单列表
     */
    public List<Consultation> selectConsultationList(Consultation consultation);

    /**
     * 创建问诊单
     */
    public int insertConsultation(Consultation consultation);

    /**
     * 修改问诊单
     */
    public int updateConsultation(Consultation consultation);

    /**
     * 删除问诊单
     */
    public int deleteConsultationByIds(Long[] consultationIds);

    /**
     * 开始问诊（医生接诊）
     */
    public int startConsultation(Long consultationId, Long doctorId);

    /**
     * 结束问诊
     */
    public int endConsultation(Long consultationId);

    /**
     * 取消问诊
     */
    public int cancelConsultation(Long consultationId);

    /**
     * 判断用户是否是问诊单参与者，返回用户类型（1=医生 2=病人），null表示非参与者
     */
    public String resolveUserRole(Long consultationId, Long userId);
}
