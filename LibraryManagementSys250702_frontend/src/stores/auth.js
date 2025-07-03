import { defineStore } from 'pinia'
import { auth } from '@/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    isLoading: false
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.userType === 'ADMIN',
    isCustomer: (state) => state.user?.userType === 'CUSTOMER'
  },

  actions: {
    async login(credentials) {
      this.isLoading = true
      try {
        const response = await auth.login(credentials)
        this.token = response.data.token
        this.user = response.data.user
        
        localStorage.setItem('token', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
        
        return response
      } catch (error) {
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async register(userData) {
      this.isLoading = true
      try {
        const response = await auth.register(userData)
        return response
      } catch (error) {
        throw error
      } finally {
        this.isLoading = false
      }
    },

    async logout() {
      try {
        await auth.logout()
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.user = null
        this.token = null
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    },

    clearAuth() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
}) 