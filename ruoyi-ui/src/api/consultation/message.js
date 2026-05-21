import request from '@/utils/request'

// 查询问诊消息列表
export function listMessages(consultationId, query) {
  return request({
    url: '/consultation/message/list/' + consultationId,
    method: 'get',
    params: query
  })
}

// 发送消息
export function sendMessage(data) {
  return request({
    url: '/consultation/message',
    method: 'post',
    data: data
  })
}
