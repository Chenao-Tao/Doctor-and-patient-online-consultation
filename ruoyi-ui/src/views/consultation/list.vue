<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="问诊状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="待接诊" value="0" />
          <el-option label="进行中" value="1" />
          <el-option label="已结束" value="2" />
          <el-option label="已取消" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="问诊标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['consultation:consultation:add']">新增问诊</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="consultationList">
      <el-table-column label="问诊ID" align="center" prop="consultationId" width="80" />
      <el-table-column label="病人" align="center" prop="patientName" width="100" />
      <el-table-column label="医生" align="center" prop="doctorName" width="100" />
      <el-table-column label="问诊标题" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">查看</el-button>
          <el-button link type="success" @click="handleStart(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['consultation:consultation:edit']">接诊</el-button>
          <el-button link type="primary" @click="handleEnterRoom(scope.row)" v-if="scope.row.status === '1'" v-hasPermi="['consultation:consultation:token']">进入房间</el-button>
          <el-button link type="danger" @click="handleCancel(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['consultation:consultation:edit']">取消</el-button>
          <el-button link type="warning" @click="handleEnd(scope.row)" v-if="scope.row.status === '1'" v-hasPermi="['consultation:consultation:edit']">结束</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增问诊对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" append-to-body>
      <el-form ref="consultationRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="病人" prop="patientId">
          <el-select v-model="form.patientId" placeholder="请选择病人" style="width: 100%">
            <el-option v-for="p in patientList" :key="p.userId" :label="p.nickName" :value="p.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生" prop="doctorId">
          <el-select v-model="form.doctorId" placeholder="请选择医生" style="width: 100%">
            <el-option v-for="doc in doctorList" :key="doc.userId" :label="doc.nickName" :value="doc.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="主诉" prop="title">
          <el-input v-model="form.title" placeholder="请输入主诉" />
        </el-form-item>
        <el-form-item label="病情描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述病情" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listConsultation, addConsultation, startConsultation, endConsultation, cancelConsultation } from '@/api/consultation/consultation'
import { listUser } from '@/api/system/user'

const router = useRouter()

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const consultationList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const doctorList = ref([])
const patientList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: undefined,
  title: undefined
})

const form = ref({})
const rules = {
  patientId: [{ required: true, message: '病人不能为空', trigger: 'change' }],
  doctorId: [{ required: true, message: '医生不能为空', trigger: 'change' }],
  title: [{ required: true, message: '主诉不能为空', trigger: 'blur' }]
}

function statusText(status) {
  const map = { '0': '待接诊', '1': '进行中', '2': '已结束', '3': '已取消' }
  return map[status] || '未知'
}

function statusTagType(status) {
  const map = { '0': 'info', '1': 'success', '2': '', '3': 'warning' }
  return map[status] || 'info'
}

function getList() {
  loading.value = true
  listConsultation(queryParams).then(res => {
    consultationList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.status = undefined
  queryParams.title = undefined
  handleQuery()
}

function handleAdd() {
  form.value = { patientId: undefined, doctorId: undefined, title: '', description: '' }
  dialogTitle.value = '新增问诊'
  dialogVisible.value = true
}

function loadUsers() {
  listUser({ pageNum: 1, pageSize: 1000 }).then(res => {
    const rows = res.rows || []
    doctorList.value = rows
    patientList.value = rows
  })
}

function submitForm() {
  addConsultation(form.value).then(() => {
    ElMessage.success('新增成功')
    dialogVisible.value = false
    getList()
  })
}

function handleDetail(row) {
  router.push('/consultation/detail/' + row.consultationId)
}

function handleStart(row) {
  ElMessageBox.confirm('确认接诊该问诊？', '提示', { type: 'warning' }).then(() => {
    startConsultation(row.consultationId).then(() => {
      ElMessage.success('接诊成功')
      getList()
    })
  })
}

function handleEnterRoom(row) {
  router.push('/consultation/room/' + row.consultationId)
}

function handleEnd(row) {
  ElMessageBox.confirm('确认结束该问诊？', '提示', { type: 'warning' }).then(() => {
    endConsultation(row.consultationId).then(() => {
      ElMessage.success('问诊已结束')
      getList()
    })
  })
}

function handleCancel(row) {
  ElMessageBox.confirm('确认取消该问诊？', '提示', { type: 'warning' }).then(() => {
    cancelConsultation(row.consultationId).then(() => {
      ElMessage.success('问诊已取消')
      getList()
    })
  })
}

onMounted(() => {
  loadUsers()
  getList()
})
</script>
