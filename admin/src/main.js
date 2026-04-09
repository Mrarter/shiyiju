import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './styles/base.css'

// 确保 el-dialog 居中显示
const style = document.createElement('style')
style.textContent = `
  .el-dialog {
    margin: 0 auto !important;
    top: 50% !important;
    transform: translateY(-50%) !important;
  }
  .el-overlay {
    display: flex !important;
    justify-content: center !important;
    align-items: center !important;
  }
`
document.head.appendChild(style)

createApp(App).use(createPinia()).use(router).use(ElementPlus).mount('#app')
