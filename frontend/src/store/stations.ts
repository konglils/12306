import axios from 'axios'
import { create } from 'zustand'

interface StationsState {
  stations: Record<string, string>
  fetch: () => Promise<void>
}

export const useStations = create<StationsState>((set) => ({
  stations: {},
  fetch: async () => {
    try {
      const { data } = await axios.get('/api/stations')
      set({ stations: data })
    } catch { /* 后端未启动时静默忽略 */ }
  },
}))
