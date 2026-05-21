<template>
  <div class="livekit-room">
    <div v-if="error" class="livekit-error">
      <el-alert :title="error" type="error" show-icon :closable="false" />
    </div>
    <div v-if="connected" class="livekit-content">
      <!-- 远端参与者视频 -->
      <div class="remote-tracks">
        <div v-for="p in remoteParticipants" :key="p.identity" class="remote-participant">
          <div class="video-container" :class="{ 'active-speaker': p.isSpeaking }">
            <video
              :ref="el => setVideoRef(p.identity, el)"
              autoplay
              playsinline
              class="remote-video"
            />
            <div class="participant-label">{{ p.name || p.identity }}</div>
          </div>
        </div>
      </div>
      <!-- 本地视频（画中画） -->
      <div class="local-track">
        <video ref="localVideoRef" autoplay playsinline muted class="local-video" />
        <div class="participant-label">我</div>
      </div>
    </div>
    <div v-else-if="!error" class="livekit-connecting">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>正在连接问诊房间...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { Room, RoomEvent, Track, createLocalTracks } from 'livekit-client'
import { Loading } from '@element-plus/icons-vue'

const props = defineProps({
  token: { type: String, required: true },
  wsUrl: { type: String, required: true },
  roomName: { type: String, required: true }
})

const emit = defineEmits(['connected', 'disconnected', 'data-received', 'error'])

const room = ref(null)
const connected = ref(false)
const error = ref(null)
const remoteParticipants = ref([])
const localVideoRef = ref(null)
const videoRefs = {}

let localTracks = []

function setVideoRef(identity, el) {
  videoRefs[identity] = el
}

async function connectRoom() {
  try {
    room.value = new Room()
    // 监听远端轨道订阅
    room.value.on(RoomEvent.TrackSubscribed, (track, publication, participant) => {
      if (track.kind === Track.Kind.Video) {
        const item = remoteParticipants.value.find(p => p.identity === participant.identity)
        if (item) {
          item.isSpeaking = false
        } else {
          remoteParticipants.value.push({
            identity: participant.identity,
            name: participant.name,
            isSpeaking: false
          })
        }
        // 延迟挂载视频元素
        setTimeout(() => {
          const el = videoRefs[participant.identity]
          if (el) {
            track.attach(el)
          }
        }, 100)
      }
      if (track.kind === Track.Kind.Audio) {
        const audioEl = document.createElement('audio')
        audioEl.id = `audio-${participant.identity}`
        audioEl.autoplay = true
        document.body.appendChild(audioEl)
        track.attach(audioEl)
      }
    })

    room.value.on(RoomEvent.TrackUnsubscribed, (track, publication, participant) => {
      if (track.kind === Track.Kind.Video) {
        track.detach().forEach(el => el.remove())
        remoteParticipants.value = remoteParticipants.value.filter(p => p.identity !== participant.identity)
      }
      if (track.kind === Track.Kind.Audio) {
        track.detach().forEach(el => el.remove())
        const audioEl = document.getElementById(`audio-${participant.identity}`)
        if (audioEl) audioEl.remove()
      }
    })

    room.value.on(RoomEvent.DataReceived, (payload, participant) => {
      const decoder = new TextDecoder()
      const text = decoder.decode(payload)
      try {
        const data = JSON.parse(text)
        emit('data-received', { ...data, senderIdentity: participant.identity })
      } catch {
        emit('data-received', { text, senderIdentity: participant.identity })
      }
    })

    room.value.on(RoomEvent.Disconnected, () => {
      connected.value = false
      emit('disconnected')
    })

    room.value.on(RoomEvent.ParticipantConnected, (participant) => {
      // 新参与者加入
    })

    room.value.on(RoomEvent.ParticipantDisconnected, (participant) => {
      remoteParticipants.value = remoteParticipants.value.filter(p => p.identity !== participant.identity)
    })

    // 连接房间
    await room.value.connect(props.wsUrl, props.token)
    connected.value = true
    emit('connected')

    // 发布本地音视频
    localTracks = await createLocalTracks({ audio: true, video: true })
    for (const track of localTracks) {
      await room.value.localParticipant.publishTrack(track)
      if (track.kind === Track.Kind.Video && localVideoRef.value) {
        track.attach(localVideoRef.value)
      }
    }
  } catch (e) {
    error.value = '连接失败：' + (e.message || '未知错误')
    emit('error', error.value)
  }
}

async function disconnectRoom() {
  if (room.value) {
    // 停止本地轨道
    for (const track of localTracks) {
      track.stop()
      track.detach()
    }
    localTracks = []
    await room.value.disconnect()
    room.value = null
    connected.value = false
  }
}

function sendData(data) {
  if (room.value && connected.value) {
    const encoder = new TextEncoder()
    const payload = encoder.encode(JSON.stringify(data))
    room.value.localParticipant.publishData(payload)
  }
}

async function toggleCamera() {
  if (room.value) {
    const cam = room.value.localParticipant.getTrackPublication(Track.Source.Camera)
    if (cam) {
      await cam.track.setEnabled(!cam.track.enabled)
    }
  }
}

async function toggleMic() {
  if (room.value) {
    const mic = room.value.localParticipant.getTrackPublication(Track.Source.Microphone)
    if (mic) {
      await mic.track.setEnabled(!mic.track.enabled)
    }
  }
}

onMounted(() => {
  if (props.token && props.wsUrl) {
    connectRoom()
  }
})

onUnmounted(() => {
  disconnectRoom()
})

defineExpose({ sendData, toggleCamera, toggleMic, disconnect: disconnectRoom, connected })
</script>

<style scoped>
.livekit-room {
  width: 100%;
  height: 100%;
  position: relative;
  background: #1a1a2e;
}
.livekit-content {
  width: 100%;
  height: 100%;
  position: relative;
}
.remote-tracks {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px;
}
.remote-participant {
  flex: 1;
  max-width: 100%;
  height: 100%;
}
.video-container {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
}
.video-container.active-speaker {
  border-color: var(--el-color-primary);
}
.remote-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.local-track {
  position: absolute;
  bottom: 80px;
  right: 16px;
  width: 180px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid rgba(255,255,255,0.3);
  z-index: 10;
}
.local-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scaleX(-1);
}
.participant-label {
  position: absolute;
  bottom: 4px;
  left: 8px;
  color: #fff;
  font-size: 12px;
  background: rgba(0,0,0,0.6);
  padding: 2px 8px;
  border-radius: 4px;
}
.livekit-connecting {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #fff;
  gap: 12px;
  font-size: 16px;
}
.livekit-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
}
</style>
