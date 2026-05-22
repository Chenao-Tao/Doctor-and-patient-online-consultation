package com.ruoyi.consultation.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.consultation.domain.Consultation;
import com.ruoyi.consultation.domain.ConsultationMessage;
import com.ruoyi.consultation.service.IConsultationService;
import com.ruoyi.consultation.service.IConsultationMessageService;

/**
 * 问诊消息记录Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/consultation/message")
public class ConsultationMessageController extends BaseController
{
    @Autowired
    private IConsultationMessageService messageService;

    @Autowired
    private IConsultationService consultationService;

    /**
     * 查询问诊消息列表
     */
    @PreAuthorize("@ss.hasPermi('consultation:message:list')")
    @GetMapping("/list/{consultationId}")
    public TableDataInfo list(@PathVariable Long consultationId)
    {
        Consultation consultation = consultationService.selectConsultationById(consultationId);
        if (consultation == null || !canAccessConsultation(consultation))
        {
            return getDataTable(List.of());
        }
        startPage();
        List<ConsultationMessage> list = messageService.selectMessagesByConsultationId(consultationId);
        return getDataTable(list);
    }

    /**
     * 发送消息
     */
    @PreAuthorize("@ss.hasPermi('consultation:message:add')")
    @PostMapping
    public AjaxResult add(@RequestBody ConsultationMessage message)
    {
        if (message.getConsultationId() == null)
        {
            return error("问诊ID不能为空");
        }
        Consultation consultation = consultationService.selectConsultationById(message.getConsultationId());
        if (consultation == null)
        {
            return error("问诊不存在");
        }
        if (!"1".equals(consultation.getStatus()))
        {
            return error("问诊未开始或已结束，无法发送消息");
        }

        String role = consultationService.resolveUserRole(message.getConsultationId(), getUserId());
        if (role == null)
        {
            return error("无权发送消息");
        }

        message.setSenderId(getUserId());
        message.setSenderType(role);
        if (message.getMessageType() == null || message.getMessageType().isEmpty())
        {
            message.setMessageType("1");
        }
        return toAjax(messageService.insertMessage(message));
    }

    private boolean canAccessConsultation(Consultation consultation)
    {
        return SecurityUtils.isAdmin()
                || getUserId().equals(consultation.getDoctorId())
                || getUserId().equals(consultation.getPatientId());
    }
}
