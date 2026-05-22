<template>
  <div class="consultation-room">
    <!-- 顶部栏 -->
    <div class="room-header">
      <el-button text @click="handleBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>返回
      </el-button>
      <span class="room-title">问诊 #{{ consultationId }}</span>
      <el-button type="danger" size="small" @click="handleEnd" :disabled="!isConnected">结束问诊</el-button>
    </div>

    <div class="room-body">
      <!-- 左侧：音视频区域 -->
      <div class="room-video">
        <LiveKitRoom
          v-if="tokenInfo.token"
          ref="livekitRef"
          :token="tokenInfo.token"
          :wsUrl="tokenInfo.wsUrl"
          :roomName="tokenInfo.roomName"
          @connected="onConnected"
          @disconnected="onDisconnected"
          @data-received="onDataReceived"
          @error="onError"
        />
      </div>

      <!-- 右侧：聊天面板 -->
      <div class="room-chat">
        <div class="chat-header">消息</div>
        <div class="chat-messages" ref="chatMessagesRef">
          <div v-for="msg in chatMessages" :key="msg.id" class="chat-msg" :class="{ 'self': msg.isSelf }">
            <div class="chat-msg-sender">{{ msg.sender }}</div>
            <div class="chat-msg-content">{{ msg.content }}</div>
            <div class="chat-msg-time">{{ msg.time }}</div>
          </div>
          <div v-if="chatMessages.length === 0" class="chat-empty">暂无消息</div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            placeholder="输入消息..."
            @keyup.enter="sendMessage"
            :disabled="!isConnected"
          >
            <template #append>
              <el-button @click="sendMessage" :disabled="!isConnected">发送</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <!-- 底部控制栏 -->
    <div class="room-controls">
      <el-tooltip content="麦克风">
        <el-button :type="micEnabled ? 'primary' : 'default'" circle @click="toggleMic" :disabled="!isConnected">
          <el-icon><Microphone /></el-icon>
        </el-button>
      </el-tooltip>
      <el-tooltip content="摄像头">
        <el-button :type="cameraEnabled ? 'primary' : 'default'" circle @click="toggleCamera" :disabled="!isConnected">
          <el-icon><VideoCamera /></el-icon>
        </el-button>
      </el-tooltip>
      <el-tooltip content="挂断">
        <el-button type="danger" circle @click="handleDisconnect" :disabled="!isConnected">
          <el-icon><PhoneFilled /></el-icon>
        </el-button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Microphone, VideoCamera, PhoneFilled } from '@element-plus/icons-vue'
import { endConsultation, getConsultationToken, leaveConsultationRoom } from '@/api/consultation/consultation'
import { sendMessage as sendMessageApi } from '@/api/consultation/message'
import LiveKitRoom from '@/components/LiveKit/LiveKitRoom.vue'

const route = useRoute()
const router = useRouter()
const consultationId = route.params.id

const livekitRef = ref(null)
const chatMessagesRef = ref(null)
const tokenInfo = ref({})
const isConnected = ref(false)
const inputMessage = ref('')
const chatMessages = ref([])
const micEnabled = ref(true)
const cameraEnabled = ref(true)
const userType = ref('')
let msgIdCounter = 0
let hasEnteredRoom = false
let leaveReported = false

onMounted(async () => {
  try {
    const res = await getConsultationToken(consultationId)
    if (res.code === 200) {
      tokenInfo.value = res.data
      userType.value = res.data.userType
    } else {
      ElMessage.error(res.msg || '获取Token失败')
    }
  } catch (e) {
    ElMessage.error('获取Token失败，请检查是否有权限进入该房间')
  }
})

function onConnected() {
  isConnected.value = true
  hasEnteredRoom = true
  ElMessage.success('已连接问诊房间')
}

function onDisconnected() {
  isConnected.value = false
  reportLeave()
}

function onError(err) {
  ElMessage.error(err)
}

function onDataReceived(data) {
  addChatMessage(data.senderIdentity || '对方', data.text || data.content, false)
}

function addChatMessage(sender, content, isSelf) {
  const now = new Date()
  const time = now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0')
  chatMessages.value.push({
    id: ++msgIdCounter,
    sender,
    content,
    isSelf,
    time
  })
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

async function sendMessage() {
  const text = inputMessage.value.trim()
  if (!text || !isConnected.value) return

  // 通过 DataChannel 发送
  if (livekitRef.value) {
    livekitRef.value.sendData({
      type: 'chat',
      content: text,
      timestamp: Date.now()
    })
  }

  // 本地显示
  addChatMessage('我', text, true)

  // 持久化到数据库
  try {
    await sendMessageApi({
      consultationId: consultationId,
      messageType: '1',
      content: text
    })
  } catch (e) {
    // 持久化失败不影响实时消息
    console.warn('消息持久化失败', e)
  }

  inputMessage.value = ''
}

async function toggleMic() {
  if (livekitRef.value) {
    await livekitRef.value.toggleMic()
    micEnabled.value = !micEnabled.value
  }
}

async function toggleCamera() {
  if (livekitRef.value) {
    await livekitRef.value.toggleCamera()
    cameraEnabled.value = !cameraEnabled.value
  }
}

function handleBack() {
  ElMessageBox.confirm('确定要离开问诊房间吗？', '提示', { type: 'warning' }).then(async () => {
    if (livekitRef.value) {
      await livekitRef.value.disconnect()
    }
    await reportLeave()
    goBack()
  })
}

function handleDisconnect() {
  handleBack()
}

function handleEnd() {
  ElMessageBox.confirm('确定要结束该问诊吗？结束后将无法再进入房间。', '提示', { type: 'warning' }).then(async () => {
    if (livekitRef.value) {
      await livekitRef.value.disconnect()
    }
    await reportLeave()
    await endConsultation(consultationId)
    ElMessage.success('问诊已结束')
    goBack()
  })
}

async function reportLeave() {
  if (!hasEnteredRoom || leaveReported) return
  leaveReported = true
  try {
    await leaveConsultationRoom(consultationId)
  } catch (e) {
    console.warn('离开房间状态上报失败', e)
  }
}

function goBack() {
  router.push('/consultation/list')
}

onBeforeUnmount(() => {
  reportLeave()
})
</script>

<style scoped>
.consultation-room {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #0f0f1a;
  color: #fff;
}
.room-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background: #1a1a2e;
  border-bottom: 1px solid #333;
  min-height: 48px;
}
.back-btn {
  color: #fff !important;
}
.room-title {
  font-size: 16px;
  font-weight: 500;
}
.room-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}
.room-video {
  flex: 1;
  min-width: 0;
}
.room-chat {
  width: 320px;
  display: flex;
  flex-direction: column;
  background: #1a1a2e;
  border-left: 1px solid #333;
}
.chat-header {
  padding: 12px 16px;
  font-weight: 500;
  border-bottom: 1px solid #333;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}
.chat-msg {
  margin-bottom: 12px;
}
.chat-msg.self {
  text-align: right;
}
.chat-msg-sender {
  font-size: 12px;
  color: #aaa;
  margin-bottom: 2px;
}
.chat-msg-content {
  display: inline-block;
  background: #2a2a3e;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  max-width: 80%;
  word-break: break-all;
}
.chat-msg.self .chat-msg-content {
  background: var(--el-color-primary);
  color: #fff;
}
.chat-msg-time {
  font-size: 11px;
  color: #666;
  margin-top: 2px;
}
.chat-empty {
  text-align: center;
  color: #666;
  padding: 40px 0;
}
.chat-input {
  padding: 12px;
  border-top: 1px solid #333;
}
.room-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 12px;
  background: #1a1a2e;
  border-top: 1px solid #333;
}
</style>
