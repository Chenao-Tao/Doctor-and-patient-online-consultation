package com.ruoyi.consultation.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.consultation.domain.Consultation;
import com.ruoyi.consultation.domain.ConsultationParticipant;
import com.ruoyi.consultation.service.IConsultationService;
import com.ruoyi.consultation.service.IConsultationParticipantService;

/**
 * 问诊参与者Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/consultation/participant")
public class ConsultationParticipantController extends BaseController
{
    @Autowired
    private IConsultationParticipantService participantService;

    @Autowired
    private IConsultationService consultationService;

    /**
     * 查询问诊的参与者列表
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:list')")
    @GetMapping("/list/{consultationId}")
    public AjaxResult list(@PathVariable Long consultationId)
    {
        Consultation consultation = consultationService.selectConsultationById(consultationId);
        if (consultation == null)
        {
            return error("问诊不存在");
        }
        if (!canAccessConsultation(consultation))
        {
            return error("无权查看该问诊参与者");
        }
        List<ConsultationParticipant> list = participantService.selectParticipantsByConsultationId(consultationId);
        return success(list);
    }

    private boolean canAccessConsultation(Consultation consultation)
    {
        return SecurityUtils.isAdmin()
                || getUserId().equals(consultation.getDoctorId())
                || getUserId().equals(consultation.getPatientId());
    }
}
