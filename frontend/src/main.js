import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import './styles/variables.css'
import './styles/element-overrides.css'
import './styles/reset.css'
import './styles/page-layout.css'
import './styles/layout-sidebar.css'
import './styles/auth-card.css'
import App from './App.vue'
import router from './router'

const elementLocale = {
  ...zhCn,
  el: {
    ...zhCn.el,
    datepicker: {
      ...zhCn.el.datepicker,
      confirm: '确认'
    }
  }
}

const app = createApp(App)
app.use(ElementPlus, { locale: elementLocale })
app.use(router)
app.mount('#app')
