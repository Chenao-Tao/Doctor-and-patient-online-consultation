import request from '@/utils/request'

// 查询问诊列表
export function listConsultation(query) {
  return request({
    url: '/consultation/consultation/list',
    method: 'get',
    params: query
  })
}

// 查询问诊详情
export function getConsultation(consultationId) {
  return request({
    url: '/consultation/consultation/' + consultationId,
    method: 'get'
  })
}

// 新增问诊
export function addConsultation(data) {
  return request({
    url: '/consultation/consultation',
    method: 'post',
    data: data
  })
}

// 修改问诊
export function updateConsultation(data) {
  return request({
    url: '/consultation/consultation',
    method: 'put',
    data: data
  })
}

// 删除问诊
export function delConsultation(consultationIds) {
  return request({
    url: '/consultation/consultation/' + consultationIds,
    method: 'delete'
  })
}

// 开始问诊（医生接诊）
export function startConsultation(consultationId) {
  return request({
    url: '/consultation/consultation/' + consultationId + '/start',
    method: 'put'
  })
}

// 结束问诊
export function endConsultation(consultationId) {
  return request({
    url: '/consultation/consultation/' + consultationId + '/end',
    method: 'put'
  })
}

// 取消问诊
export function cancelConsultation(consultationId) {
  return request({
    url: '/consultation/consultation/' + consultationId + '/cancel',
    method: 'put'
  })
}

// 获取LiveKit Token
export function getConsultationToken(consultationId) {
  return request({
    url: '/consultation/consultation/' + consultationId + '/token',
    method: 'get'
  })
}

// 离开LiveKit房间
export function leaveConsultationRoom(consultationId) {
  return request({
    url: '/consultation/consultation/' + consultationId + '/leave',
    method: 'post'
  })
}

// 查询参与者列表
export function listParticipants(consultationId) {
  return request({
    url: '/consultation/participant/list/' + consultationId,
    method: 'get'
  })
}
