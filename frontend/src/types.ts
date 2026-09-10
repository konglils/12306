// 与后端 cn.nispring.rail12306.model.SeatType 的 code（@JsonValue）一一对应
export type SeatTypeCode =
  | '1' // 硬座
  | '2' // 软座
  | '3' // 硬卧
  | '4' // 软卧
  | '6' // 高级软卧
  | '7' // 一等软座
  | '8' // 二等软座
  | '9' // 商务
  | 'A' // 高级动卧
  | 'D' // 优选一等
  | 'F' // 动卧
  | 'H' // 一人软包
  | 'I' // 一等卧
  | 'J' // 二等卧
  | 'M' // 一等
  | 'O' // 二等
  | 'P' // 特等
  | 'Q' // 多功能座

export const SEAT_TYPE_LABEL: Record<SeatTypeCode, string> = {
  '1': '硬座',
  '2': '软座',
  '3': '硬卧',
  '4': '软卧',
  '6': '高级软卧',
  '7': '一等软座',
  '8': '二等软座',
  '9': '商务座',
  'A': '高级动卧',
  'D': '优选一等',
  'F': '动卧',
  'H': '一人软包',
  'I': '一等卧',
  'J': '二等卧',
  'M': '一等座',
  'O': '二等座',
  'P': '特等座',
  'Q': '多功能座',
}

export interface Seat {
  type: SeatTypeCode
  hasSeat: boolean
  price: number
  remaining: number
}

export interface Ticket {
  trainCode: string
  fromTelecode: string
  toTelecode: string
  startTime: string
  arriveDay: number
  arriveTime: string
  seats: Seat[]
}

export interface TimeTableRow {
  stationTelecode: string
  trainCode: string
  arriveDay: number | null
  arriveTime: string | null
  startDay: number | null
  startTime: string | null
}
