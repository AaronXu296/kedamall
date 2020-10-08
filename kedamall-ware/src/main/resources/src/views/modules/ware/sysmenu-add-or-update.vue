<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="父菜单ID，一级菜单为0" prop="parentId">
      <el-input v-model="dataForm.parentId" placeholder="父菜单ID，一级菜单为0"></el-input>
    </el-form-item>
    <el-form-item label="菜单名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="菜单名称"></el-input>
    </el-form-item>
    <el-form-item label="菜单URL" prop="url">
      <el-input v-model="dataForm.url" placeholder="菜单URL"></el-input>
    </el-form-item>
    <el-form-item label="授权(多个用逗号分隔，如：user:list,user:create)" prop="perms">
      <el-input v-model="dataForm.perms" placeholder="授权(多个用逗号分隔，如：user:list,user:create)"></el-input>
    </el-form-item>
    <el-form-item label="类型   0：目录   1：菜单   2：按钮" prop="type">
      <el-input v-model="dataForm.type" placeholder="类型   0：目录   1：菜单   2：按钮"></el-input>
    </el-form-item>
    <el-form-item label="菜单图标" prop="icon">
      <el-input v-model="dataForm.icon" placeholder="菜单图标"></el-input>
    </el-form-item>
    <el-form-item label="排序" prop="orderNum">
      <el-input v-model="dataForm.orderNum" placeholder="排序"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          menuId: 0,
          parentId: '',
          name: '',
          url: '',
          perms: '',
          type: '',
          icon: '',
          orderNum: ''
        },
        dataRule: {
          parentId: [
            { required: true, message: '父菜单ID，一级菜单为0不能为空', trigger: 'blur' }
          ],
          name: [
            { required: true, message: '菜单名称不能为空', trigger: 'blur' }
          ],
          url: [
            { required: true, message: '菜单URL不能为空', trigger: 'blur' }
          ],
          perms: [
            { required: true, message: '授权(多个用逗号分隔，如：user:list,user:create)不能为空', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '类型   0：目录   1：菜单   2：按钮不能为空', trigger: 'blur' }
          ],
          icon: [
            { required: true, message: '菜单图标不能为空', trigger: 'blur' }
          ],
          orderNum: [
            { required: true, message: '排序不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.menuId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.menuId) {
            this.$http({
              url: this.$http.adornUrl(`/ware/sysmenu/info/${this.dataForm.menuId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.parentId = data.sysMenu.parentId
                this.dataForm.name = data.sysMenu.name
                this.dataForm.url = data.sysMenu.url
                this.dataForm.perms = data.sysMenu.perms
                this.dataForm.type = data.sysMenu.type
                this.dataForm.icon = data.sysMenu.icon
                this.dataForm.orderNum = data.sysMenu.orderNum
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/ware/sysmenu/${!this.dataForm.menuId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'menuId': this.dataForm.menuId || undefined,
                'parentId': this.dataForm.parentId,
                'name': this.dataForm.name,
                'url': this.dataForm.url,
                'perms': this.dataForm.perms,
                'type': this.dataForm.type,
                'icon': this.dataForm.icon,
                'orderNum': this.dataForm.orderNum
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
