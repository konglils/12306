import axios from 'axios'
import { create } from 'zustand'

interface Station {
  areaId: number
  telecode: string
  name: string
}

interface StationsState {
  stations: Record<string, string>
  fetch: () => Promise<void>
}

export const useStations = create<StationsState>((set) => ({
  stations: {},
  fetch: async () => {
    try {
      const { data } = await axios.get('/api/stations')
      const stationMap: Record<string, string> = {}
      for (const s of data as Station[]) stationMap[s.telecode] = s.name
      set({ stations: stationMap })
    } catch { /* 后端未启动时静默忽略 */ }
  },
}))
