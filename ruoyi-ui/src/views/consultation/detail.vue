<template>
  <div class="app-container">
    <el-card v-loading="loading">
      <template #header>
        <span>问诊详情 #{{ consultation.consultationId }}</span>
        <el-tag :type="statusTagType(consultation.status)" style="margin-left: 12px">{{ statusText(consultation.status) }}</el-tag>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="病人">{{ consultation.patientName }}</el-descriptions-item>
        <el-descriptions-item label="医生">{{ consultation.doctorName || '待分配' }}</el-descriptions-item>
        <el-descriptions-item label="主诉">{{ consultation.title }}</el-descriptions-item>
        <el-descriptions-item label="房间名">{{ consultation.roomName }}</el-descriptions-item>
        <el-descriptions-item label="病情描述" :span="2">{{ consultation.description }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ consultation.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ consultation.endTime }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ consultation.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ consultation.remark }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px; text-align: right">
        <el-button v-if="consultation.status === '0'" type="success" @click="handleStart">接诊</el-button>
        <el-button v-if="consultation.status === '1'" type="primary" @click="handleEnterRoom">进入房间</el-button>
        <el-button v-if="consultation.status === '1'" type="warning" @click="handleEnd">结束问诊</el-button>
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </el-card>

    <!-- 参与者列表 -->
    <el-card style="margin-top: 16px">
      <template #header>参与者</template>
      <el-table :data="participants" style="width: 100%">
        <el-table-column label="用户" prop="userName" />
        <el-table-column label="类型" prop="userType">
          <template #default="scope">
            {{ scope.row.userType === '1' ? '医生' : scope.row.userType === '2' ? '病人' : 'AI Agent' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'info'" size="small">
              {{ scope.row.status === '0' ? '已邀请' : scope.row.status === '1' ? '已加入' : '已离开' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="加入时间" prop="joinTime" />
        <el-table-column label="离开时间" prop="leaveTime" />
      </el-table>
    </el-card>

    <!-- 消息记录 -->
    <el-card style="margin-top: 16px">
      <template #header>消息记录</template>
      <div class="message-list">
        <div v-for="msg in messages" :key="msg.messageId" class="message-item">
          <span class="message-sender">{{ msg.senderName || '系统' }}</span>
          <span class="message-time">{{ msg.createTime }}</span>
          <div class="message-content">{{ msg.content }}</div>
        </div>
        <el-empty v-if="messages.length === 0" description="暂无消息记录" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConsultation, startConsultation, endConsultation, listParticipants } from '@/api/consultation/consultation'
import { listMessages } from '@/api/consultation/message'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const consultation = ref({})
const participants = ref([])
const messages = ref([])
const consultationId = route.params.id

function statusText(status) {
  const map = { '0': '待接诊', '1': '进行中', '2': '已结束', '3': '已取消' }
  return map[status] || '未知'
}

function statusTagType(status) {
  const map = { '0': 'info', '1': 'success', '2': '', '3': 'warning' }
  return map[status] || 'info'
}

function loadData() {
  loading.value = true
  getConsultation(consultationId).then(res => {
    consultation.value = res.data
    loading.value = false
  })
  listParticipants(consultationId).then(res => {
    participants.value = res.data || []
  })
  listMessages(consultationId).then(res => {
    messages.value = res.rows || []
  })
}

function handleStart() {
  ElMessageBox.confirm('确认接诊该问诊？', '提示', { type: 'warning' }).then(() => {
    startConsultation(consultationId).then(() => {
      ElMessage.success('接诊成功')
      loadData()
    })
  })
}

function handleEnterRoom() {
  router.push('/consultation/room/' + consultationId)
}

function handleEnd() {
  ElMessageBox.confirm('确认结束该问诊？', '提示', { type: 'warning' }).then(() => {
    endConsultation(consultationId).then(() => {
      ElMessage.success('问诊已结束')
      loadData()
    })
  })
}

function goBack() {
  router.push('/consultation/list')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.message-list {
  max-height: 400px;
  overflow-y: auto;
}
.message-item {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
.message-sender {
  font-weight: bold;
  margin-right: 8px;
}
.message-time {
  color: #999;
  font-size: 12px;
}
.message-content {
  margin-top: 4px;
  color: #333;
}
</style>
