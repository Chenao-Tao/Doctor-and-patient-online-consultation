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
import com.ruoyi.consultation.domain.ConsultationParticipant;
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

    /**
     * 查询问诊的参与者列表
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:list')")
    @GetMapping("/list/{consultationId}")
    public AjaxResult list(@PathVariable Long consultationId)
    {
        List<ConsultationParticipant> list = participantService.selectParticipantsByConsultationId(consultationId);
        return success(list);
    }
}
