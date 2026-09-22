import type { SeatTypeCode } from '@/types'

export type BerthLevel = 'lower' | 'middle' | 'upper'

export type SeatServiceInfo =
  | { kind: 'seat'; letters: string[] }
  | { kind: 'berth'; levels: BerthLevel[] }

// 各席别对应的选座/选铺服务；未列出的席别（硬座、软座、一等软座、二等软座、一人软包等）无服务
const SERVICE: Partial<Record<SeatTypeCode, SeatServiceInfo>> = {
  '3': { kind: 'berth', levels: ['lower', 'middle', 'upper'] }, // 硬卧：上中下铺
  '4': { kind: 'berth', levels: ['lower', 'upper'] }, // 软卧：上下铺
  '6': { kind: 'berth', levels: ['lower', 'upper'] }, // 高级软卧：上下铺
  '9': { kind: 'seat', letters: ['A', 'C', 'F'] }, // 商务
  A: { kind: 'berth', levels: ['lower', 'upper'] }, // 高级动卧：上下铺
  D: { kind: 'seat', letters: ['A', 'C', 'D', 'F'] }, // 优选一等
  F: { kind: 'berth', levels: ['lower', 'upper'] }, // 动卧：上下铺
  I: { kind: 'berth', levels: ['lower', 'middle', 'upper'] }, // 一等卧：上中下铺
  J: { kind: 'berth', levels: ['lower', 'upper'] }, // 二等卧：上下铺
  M: { kind: 'seat', letters: ['A', 'C', 'D', 'F'] }, // 一等
  O: { kind: 'seat', letters: ['A', 'B', 'C', 'D', 'F'] }, // 二等
  P: { kind: 'seat', letters: ['A', 'C', 'F'] }, // 特等
  Q: { kind: 'seat', letters: ['A', 'C', 'D', 'F'] }, // 多功能座
}

export function getSeatService(type: SeatTypeCode): SeatServiceInfo | null {
  return SERVICE[type] ?? null
}
