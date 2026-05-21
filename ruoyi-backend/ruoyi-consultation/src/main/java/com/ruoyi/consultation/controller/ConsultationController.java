package com.ruoyi.consultation.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.consultation.domain.Consultation;
import com.ruoyi.consultation.domain.ConsultationParticipant;
import com.ruoyi.consultation.service.IConsultationParticipantService;
import com.ruoyi.consultation.service.IConsultationService;
import com.ruoyi.consultation.service.ILiveKitTokenService;

/**
 * 问诊单Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/consultation/consultation")
public class ConsultationController extends BaseController
{
    @Autowired
    private IConsultationService consultationService;

    @Autowired
    private IConsultationParticipantService participantService;

    @Autowired
    private ILiveKitTokenService liveKitTokenService;

    /**
     * 查询问诊单列表
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:list')")
    @GetMapping("/list")
    public TableDataInfo list(Consultation consultation)
    {
        startPage();
        List<Consultation> list = consultationService.selectConsultationList(consultation);
        return getDataTable(list);
    }

    /**
     * 获取问诊单详情
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:query')")
    @GetMapping("/{consultationId}")
    public AjaxResult getInfo(@PathVariable Long consultationId)
    {
        return success(consultationService.selectConsultationById(consultationId));
    }

    /**
     * 新增问诊单
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:add')")
    @Log(title = "问诊管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Consultation consultation)
    {
        // 若未指定病人, 默认使用当前登录用户
        if (consultation.getPatientId() == null)
        {
            consultation.setPatientId(getUserId());
        }
        consultation.setCreateBy(getUsername());
        return toAjax(consultationService.insertConsultation(consultation));
    }

    /**
     * 修改问诊单
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:edit')")
    @Log(title = "问诊管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Consultation consultation)
    {
        consultation.setUpdateBy(getUsername());
        return toAjax(consultationService.updateConsultation(consultation));
    }

    /**
     * 删除问诊单
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:remove')")
    @Log(title = "问诊管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{consultationIds}")
    public AjaxResult remove(@PathVariable Long[] consultationIds)
    {
        return toAjax(consultationService.deleteConsultationByIds(consultationIds));
    }

    /**
     * 开始问诊（医生接诊）
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:edit')")
    @Log(title = "问诊管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{consultationId}/start")
    public AjaxResult start(@PathVariable Long consultationId)
    {
        return toAjax(consultationService.startConsultation(consultationId, getUserId()));
    }

    /**
     * 结束问诊
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:edit')")
    @Log(title = "问诊管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{consultationId}/end")
    public AjaxResult end(@PathVariable Long consultationId)
    {
        return toAjax(consultationService.endConsultation(consultationId));
    }

    /**
     * 取消问诊
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:edit')")
    @Log(title = "问诊管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{consultationId}/cancel")
    public AjaxResult cancel(@PathVariable Long consultationId)
    {
        return toAjax(consultationService.cancelConsultation(consultationId));
    }

    /**
     * 获取LiveKit Token（核心接口）
     */
    @PreAuthorize("@ss.hasPermi('consultation:consultation:token')")
    @GetMapping("/{consultationId}/token")
    public AjaxResult getLiveKitToken(@PathVariable Long consultationId)
    {
        Consultation consultation = consultationService.selectConsultationById(consultationId);
        if (consultation == null)
        {
            return error("问诊不存在");
        }

        // 校验当前用户是否为问诊参与者
        String userType = consultationService.resolveUserRole(consultationId, getUserId());
        if (userType == null)
        {
            return error("无权进入该问诊房间");
        }

        // 校验问诊状态
        if (!"0".equals(consultation.getStatus()) && !"1".equals(consultation.getStatus()))
        {
            return error("问诊已结束或已取消，无法进入");
        }

        // 待接诊状态只允许病人进入房间（医生需先接诊）
        if ("0".equals(consultation.getStatus()) && !"2".equals(userType))
        {
            return error("请先接诊后再进入房间");
        }

        // 获取连接信息（含Token）
        Map<String, Object> connectionInfo = liveKitTokenService.getConnectionInfo(
                getUserId(),
                getUsername(),
                consultation.getRoomName(),
                userType
        );

        // 更新参与者状态为已加入
        ConsultationParticipant participant = participantService.selectByConsultationAndUser(consultationId, getUserId());
        if (participant != null && !"1".equals(participant.getStatus()))
        {
            participant.setStatus("1");
            participant.setJoinTime(DateUtils.getNowDate());
            participant.setLivekitIdentity("user_" + getUserId());
            participantService.updateParticipant(participant);
        }

        return success(connectionInfo);
    }
}
