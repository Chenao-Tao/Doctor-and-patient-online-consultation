import { defineStore } from 'pinia'
import { getConsultation, getConsultationToken } from '@/api/consultation/consultation'

const useConsultationStore = defineStore('consultation', {
  state: () => ({
    currentConsultation: null,
    tokenInfo: null,
    connected: false
  }),
  actions: {
    async fetchConsultation(id) {
      const res = await getConsultation(id)
      this.currentConsultation = res.data
      return res.data
    },
    async fetchToken(id) {
      const res = await getConsultationToken(id)
      this.tokenInfo = res.data
      return res.data
    },
    setConnected(status) {
      this.connected = status
    },
    clearState() {
      this.currentConsultation = null
      this.tokenInfo = null
      this.connected = false
    }
  }
})

export default useConsultationStore
