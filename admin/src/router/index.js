import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '../layout/AdminLayout.vue'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/dashboard/DashboardView.vue'
import OperationsView from '../views/operations/OperationsView.vue'
import ArtistsView from '../views/artists/ArtistsView.vue'
import ArtworksView from '../views/artworks/ArtworksView.vue'
import UsersView from '../views/users/UsersView.vue'
import OrdersView from '../views/orders/OrdersView.vue'
import DistributorsView from '../views/distributors/DistributorsView.vue'
import SettingsView from '../views/settings/SettingsView.vue'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', component: LoginView },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: DashboardView },
      { path: 'operations', component: OperationsView },
      { path: 'artists', component: ArtistsView },
      { path: 'artworks', component: ArtworksView },
      { path: 'users', component: UsersView },
      { path: 'orders', component: OrdersView },
      { path: 'distributors', component: DistributorsView },
      { path: 'settings', component: SettingsView }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  if (to.path === '/login') {
    if (authStore.isLoggedIn) return '/dashboard'
    return true
  }
  if (!authStore.isLoggedIn) return '/login'
  if (!authStore.user) {
    try {
      await authStore.fetchMe()
    } catch {
      authStore.clearAuth()
      return '/login'
    }
  }
  return true
})

export default router
