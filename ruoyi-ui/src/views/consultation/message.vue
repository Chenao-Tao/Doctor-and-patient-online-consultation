<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true">
      <el-form-item label="问诊ID" prop="consultationId">
        <el-input v-model="queryParams.consultationId" placeholder="请输入问诊ID" clearable style="width: 150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="messageList">
      <el-table-column label="消息ID" align="center" prop="messageId" width="80" />
      <el-table-column label="问诊ID" align="center" prop="consultationId" width="80" />
      <el-table-column label="发送者" align="center" prop="senderName" width="100" />
      <el-table-column label="类型" align="center" prop="senderType" width="80">
        <template #default="scope">
          <el-tag size="small">{{ scope.row.senderType === '1' ? '医生' : scope.row.senderType === '2' ? '病人' : 'AI' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="内容" align="center" prop="content" :show-overflow-tooltip="true" />
      <el-table-column label="时间" align="center" prop="createTime" width="160" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listMessages } from '@/api/consultation/message'

const loading = ref(false)
const total = ref(0)
const messageList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  consultationId: undefined
})

function getList() {
  if (!queryParams.consultationId) {
    messageList.value = []
    total.value = 0
    return
  }
  loading.value = true
  listMessages(queryParams.consultationId, queryParams).then(res => {
    messageList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.consultationId = undefined
  handleQuery()
}
</script>
