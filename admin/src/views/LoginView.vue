<template>
  <div class="login-page">
    <div class="login-side">
      <div>
        <div class="eyebrow">拾艺局 0.7.0</div>
        <h1>运营后台</h1>
        <p>用于管理首页推荐、艺术家、作品、用户与订单，支撑小程序上线后的日常运营。</p>
      </div>
    </div>
    <div class="login-panel section-card">
      <h2>欢迎登录</h2>
      <el-form label-position="top">
        <el-form-item label="账号">
          <el-input placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-checkbox>记住登录状态</el-checkbox>
        <el-button type="primary" size="large" class="login-btn" @click="$router.push('/dashboard')">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  background: linear-gradient(135deg, #0f0f10, #2a251d);
}

.login-side {
  color: #fff;
  padding: 80px;
  display: flex;
  align-items: center;
}

.eyebrow {
  color: #f4d79c;
  margin-bottom: 16px;
}

h1 {
  font-size: 52px;
  margin: 0 0 16px;
}

p {
  max-width: 520px;
  color: rgba(255, 255, 255, 0.75);
  line-height: 1.8;
}

.login-panel {
  margin: auto 80px auto 0;
  padding: 32px;
  max-width: 420px;
}

.login-btn {
  width: 100%;
  margin-top: 20px;
}
</style>
