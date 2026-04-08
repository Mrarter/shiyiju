<template>
  <div class="login-page">
    <div class="login-shell">
      <section class="login-side">
        <div class="brand-block">
          <div class="eyebrow">拾艺局 运营后台 v0.1</div>
          <h1>运营后台登录</h1>
          <p class="intro">
            用于管理首页推荐、艺术家、作品、用户与订单，支持 PC 端重度运营，也支持移动端快速查看与处理。
          </p>

          <div class="feature-list">
            <div class="feature-card">
              <div class="feature-title">首页运营</div>
              <div class="feature-desc">Banner、推荐位、首页模块配置</div>
            </div>
            <div class="feature-card">
              <div class="feature-title">内容管理</div>
              <div class="feature-desc">艺术家、作品、状态与推荐管理</div>
            </div>
            <div class="feature-card">
              <div class="feature-title">订单与用户</div>
              <div class="feature-desc">订单处理、备注、物流与用户查看</div>
            </div>
          </div>
        </div>
      </section>

      <section class="login-panel section-card">
        <div class="panel-head">
          <div>
            <div class="panel-eyebrow">欢迎回来</div>
            <h2>登录后台</h2>
          </div>
        </div>

        <el-form label-position="top" @submit.prevent>
          <el-form-item label="账号">
            <el-input v-model="form.username" placeholder="请输入账号" size="large" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-alert
            v-if="errorMessage"
            :title="errorMessage"
            type="error"
            :closable="false"
            style="margin-bottom: 16px"
          />

          <div class="form-row">
            <el-checkbox v-model="rememberMe">记住登录状态</el-checkbox>
            <span class="hint-inline">建议仅在可信设备开启</span>
          </div>

          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>

          <div class="hint-block">
            <div>默认演示账号：admin / 123456</div>
            <div>登录成功后进入控制台首页</div>
          </div>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const errorMessage = ref('')
const rememberMe = ref(true)
const form = reactive({
  username: 'admin',
  password: '123456'
})

async function handleLogin() {
  loading.value = true
  errorMessage.value = ''
  try {
    await authStore.login(form)
    await router.push('/dashboard')
  } catch (error) {
    errorMessage.value = error.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(214, 176, 116, 0.16), transparent 30%),
    linear-gradient(135deg, #0b0a08 0%, #17130f 48%, #241d18 100%);
  padding: 24px;
  box-sizing: border-box;
}

.login-shell {
  min-height: calc(100vh - 48px);
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(360px, 440px);
  gap: 24px;
  align-items: stretch;
}

.login-side,
.login-panel {
  border-radius: 28px;
}

.login-side {
  position: relative;
  overflow: hidden;
  padding: 56px;
  color: #fff;
  background:
    linear-gradient(180deg, rgba(255,255,255,0.03), rgba(255,255,255,0.01)),
    linear-gradient(135deg, #16110e, #0d0b09 60%, #1f1914);
  border: 1px solid rgba(214, 176, 116, 0.12);
}

.brand-block {
  max-width: 640px;
}

.eyebrow,
.panel-eyebrow {
  color: #f4d79c;
  font-size: 14px;
  margin-bottom: 14px;
  letter-spacing: 0.5px;
}

h1 {
  font-size: 52px;
  line-height: 1.15;
  margin: 0;
}

.intro {
  margin-top: 18px;
  max-width: 620px;
  color: rgba(255, 255, 255, 0.74);
  line-height: 1.8;
  font-size: 16px;
}

.feature-list {
  margin-top: 34px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.feature-card {
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(214, 176, 116, 0.08);
}

.feature-title {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.feature-desc {
  margin-top: 10px;
  font-size: 13px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.68);
}

.login-panel {
  margin: auto 0;
  padding: 32px;
  width: 100%;
  background: rgba(20, 17, 14, 0.92);
  border: 1px solid rgba(214, 176, 116, 0.12);
  backdrop-filter: blur(12px);
  box-sizing: border-box;
}

.panel-head {
  margin-bottom: 18px;
}

h2 {
  margin: 0;
  font-size: 30px;
  color: #f5efe8;
}

.form-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.hint-inline,
.hint-block {
  color: #9d9489;
  font-size: 12px;
  line-height: 1.8;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
  height: 44px;
}

.hint-block {
  margin-top: 14px;
}

@media (max-width: 1080px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .login-side {
    padding: 36px;
  }

  .feature-list {
    grid-template-columns: 1fr;
  }

  .login-panel {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .login-page {
    padding: 14px;
  }

  .login-shell {
    min-height: calc(100vh - 28px);
    gap: 14px;
  }

  .login-side {
    padding: 24px;
    border-radius: 22px;
  }

  h1 {
    font-size: 34px;
  }

  .intro {
    font-size: 14px;
    line-height: 1.7;
  }

  .login-panel {
    padding: 22px 18px;
    border-radius: 22px;
  }

  h2 {
    font-size: 24px;
  }

  .form-row {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
