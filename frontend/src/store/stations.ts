import axios from 'axios'
import { create } from 'zustand'

interface Station {
  areaId: number
  telecode: string
  name: string
}

interface StationsState {
  // 车站电报码 -> 站名（选择器展示用）
  stations: Record<string, string>
  // 车站电报码 -> 所属城市区域 id（/tickets 入参需要 areaId）
  areaIdByTelecode: Record<string, number>
  fetch: () => Promise<void>
}

export const useStations = create<StationsState>((set) => ({
  stations: {},
  areaIdByTelecode: {},
  fetch: async () => {
    try {
      const { data } = await axios.get('/api/stations')
      const stationMap: Record<string, string> = {}
      const areaMap: Record<string, number> = {}
      for (const s of data as Station[]) {
        stationMap[s.telecode] = s.name
        areaMap[s.telecode] = s.areaId
      }
      set({ stations: stationMap, areaIdByTelecode: areaMap })
    } catch { /* 后端未启动时静默忽略 */ }
  },
}))
