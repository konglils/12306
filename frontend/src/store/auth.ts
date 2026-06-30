import axios from 'axios'
import { create } from 'zustand'

interface AuthState {
  username: string | null
  check: () => Promise<void>
  signin: (username: string, password: string) => Promise<void>
  signout: () => Promise<void>
}

export const useAuth = create<AuthState>((set) => ({
  username: null,

  check: async () => {
    try {
      const res = await axios.get('/api/session')
      if (res.status === 200) set({ username: res.data.username })
    } catch { /* 204 or error, stay null */ }
  },

  signin: async (username, password) => {
    await axios.post('/api/sessions', { username, password })
    set({ username })
  },

  signout: async () => {
    await axios.delete('/api/session')
    set({ username: null })
  },
}))
