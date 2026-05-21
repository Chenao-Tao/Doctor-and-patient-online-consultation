package com.ruoyi.consultation.mapper;

import java.util.List;
import com.ruoyi.consultation.domain.Consultation;

/**
 * 问诊单Mapper接口
 *
 * @author ruoyi
 */
public interface ConsultationMapper
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
     * 新增问诊单
     */
    public int insertConsultation(Consultation consultation);

    /**
     * 修改问诊单
     */
    public int updateConsultation(Consultation consultation);

    /**
     * 删除问诊单
     */
    public int deleteConsultationById(Long consultationId);

    /**
     * 批量删除问诊单
     */
    public int deleteConsultationByIds(Long[] consultationIds);
}
