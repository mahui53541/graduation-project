<template>
  <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="80">
    <FormItem label="拾取物品" prop="foundName">
      <Input v-model="formValidate.foundName" placeholder="拾取物品名称"></Input>
    </FormItem>
    <FormItem label="详细描述" prop="foundDescription">
      <Input type="textarea" :rows="3" v-model="formValidate.foundDescription"
             placeholder="拾取详细描述，请尽量描述拾取时间、地点等关键信息，当涉及贵重物品时建议隐藏关键信息，以防冒领！！！"></Input>
    </FormItem>
    <Row>
      <Col span="12">
        <FormItem prop="foundDatetime" label="拾取时间">
          <DatePicker type="datetime" placeholder="拾取时间" v-model="formValidate.foundDatetime"></DatePicker>
        </FormItem>
      </Col>
      <Col span="12">
        <FormItem label="防冒领？" prop="setFalseClaim">
          <RadioGroup v-model="formValidate.setFalseClaim">
            <Radio label="0">不开启</Radio>
            <Radio label="1">开启</Radio>
          </RadioGroup>
        </FormItem>
      </Col>
    </Row>
    <Row>
      <Col span="9">
        <FormItem label="" prop="oneLabel">
          <Input v-model="formValidate.oneLabel" placeholder="标签名称,长度1-4" style="padding: 10px 0"></Input>
        </FormItem>
      </Col>
      <Col span="15" style="padding: 13px">
        <Button icon="ios-plus-empty" type="dashed" size="small" @click="handleAdd"
                :disabled="formValidate.oneLabel.length==0||formValidate.oneLabel.length>4">添加标签
        </Button>
        <Tag color="yellow" v-for="item in formValidate.label" :key="item" :name="item" closable
             @on-close="handleClose">{{item}}
        </Tag>
      </Col>
    </Row>
    <Row>
      <Col span="16">
        <FormItem label="">
          <Upload
            ref="upload"
            action=""
            accept="image/*"
            :before-upload="handleUpload"
            type="drag">
            <div style="padding: 20px 0">
              <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
              <p>点击或拖拽图片至此上传，仅支持上传一张图片</p>
            </div>
          </Upload>
        </FormItem>
      </Col>
      <Col span="8">
        <div class="demo-upload-list" v-if="imageSrc!=null && imageSrc.length>0">
          <template>
            <img :src="imageSrc">
            <div class="demo-upload-list-cover">
              <Icon type="ios-trash-outline" @click.native="handleRemove"></Icon>
            </div>
          </template>
        </div>

      </Col>
    </Row>

    <FormItem>
      <Button type="success" @click="handleSubmit('formValidate')">提交</Button>
      <Button type="ghost" @click="handleReset('formValidate')" style="margin-left: 8px">重置</Button>
    </FormItem>
  </Form>
</template>
<script>
  import {mapState} from 'vuex'

  export default {
    name: 'found-add',
    data() {
      return {
        spinShow: false,
        format: ['jpg', 'jpeg', 'png'],
        file: null,
        imageSrc: '',
        maxSize: 2048,
        formValidate: {
          foundName: '',
          setFalseClaim: '0',
          foundDatetime: '',
          foundDescription: '',
          base64: '',
          label: [],
          oneLabel: ''
        },
        ruleValidate: {
          foundName: [
            {required: true, message: '拾取物品名称不能为空', trigger: 'blur'}
          ],
          setFalseClaim: [
            {required: true, message: '选择是否开启', trigger: 'blur'}
          ],
          foundDescription: [
            {required: true, message: '物品详细描述不能为空', trigger: 'blur'}
          ],
          oneLabel: [
            {required: false, type: 'string', min: 1, max: 4, message: '标签长度1-4', trigger: 'change'}
          ],
          interest: [
            {required: true, type: 'array', min: 1, message: 'Choose at least one hobby', trigger: 'change'},
            {type: 'array', max: 2, message: 'Choose two hobbies at best', trigger: 'change'}
          ],
          foundDatetime: [
            {required: true, type: 'date', message: '请选择拾取日期', trigger: 'change'}
          ]
        }
      }
    },
    computed: {
      ...mapState({
        // 箭头函数可使代码更简练
        user: 'user'
      }),
      foundVO() {
        return {
          foundName: this.formValidate.foundName,
          setFalseClaim: this.formValidate.setFalseClaim=="1",
          foundDatetime: this.formValidate.foundDatetime.getTime(),
          hasReviewed: true,
          hasPassed: true,
          isFound:false,
          deleted: false,
          foundDescription: this.formValidate.foundDescription,
          file: this.formValidate.base64,
          submitDatetime: new Date().getTime(),
          label: this.formValidate.label.join("#"),
          userId: this.user.id
        }
      },
    },
    methods: {
      handleSubmit(name) {
        this.$refs[name].validate((valid) => {
          if (valid) {
            this.$Modal.confirm({
              title: '确认提示',
              content: '<p>确定提交吗？</p>',
              onOk: () => {
                this.spinShow = true
                this.axios.post('/api/found',
                  this.foundVO
                ).then((res) => {
                  this.spinShow = false
                  if (res.data == 1) {
                    this.$router.push({
                      name:'lost',
                      params: { page: 1,rows:20 },
                      query:{
                        keyword:this.foundVO.foundName,
                        endDate:this.foundVO.foundDatetime
                      }
                    });
                  } else {
                    this.$Message.error({
                      content: '保存失败！',
                      duration: 3,
                      closable: true
                    });
                  }
                }).catch(function (err) {
                  console.log(err)
                })
              }
            });
          } else {
            this.$Message.error('数据填写不完整！');
          }
        })
      },
      handleReset(name) {
        this.$refs[name].resetFields();
        this.handleRemove()
      },
      handleAdd() {
        this.formValidate.label.push(this.formValidate.oneLabel)
        this.formValidate.oneLabel = ""
      },
      handleClose(event, name) {
        const index = this.formValidate.label.indexOf(name);
        this.formValidate.label.splice(index, 1);
      },
      handleUpload(file) {
        // check format
        if (this.format.length) {
          const _file_format = file.name.split('.').pop().toLocaleLowerCase();
          const checked = this.format.some(item => item.toLocaleLowerCase() === _file_format);
          if (!checked) {
            this.$Notice.warning({
              title: '文件格式错误',
              desc: file.name + '的文件格式错误, 仅支持jpg,jpef和png'
            });
            return false;
          }
        }

        // check maxSize
        if (this.maxSize) {
          if (file.size > this.maxSize * 1024) {
            this.$Notice.warning({
              title: '文件太大',
              desc: file.name + '文件大小不得超过 2M.'
            });
            return false;
          }
        }
        let reader = new FileReader()
        reader.addEventListener('load', e => {
          this.imageSrc = e.target.result
          let [, base64] = e.target.result.split(',')
          this.formValidate.base64 = base64;
        })
        reader.readAsDataURL(file)
        this.file = file;
        return false;
      },
      handleRemove() {
        this.formValidate.base64 = null;
        this.imageSrc = null;
        this.file = null;
      }
    }
  }
</script>
<style scoped>
  .demo-upload-list {
    display: inline-block;
    width: 130px;
    height: 130px;
    text-align: center;
    line-height: 130px;
    border: 1px solid transparent;
    border-radius: 4px;
    overflow: hidden;
    background: #fff;
    position: relative;
    box-shadow: 0 1px 1px rgba(0, 0, 0, .2);
    margin-left: 20px;
  }

  .demo-upload-list img {
    width: 100%;
    height: 100%;
  }

  .demo-upload-list-cover {
    display: none;
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, .6);
  }

  .demo-upload-list-cover i {
    color: #fff;
    font-size: 20px;
    cursor: pointer;
    margin: 35% auto;
  }

  .demo-upload-list:hover .demo-upload-list-cover {
    display: block;
  }
</style>

