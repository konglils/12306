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

// 与后端 cn.nispring.rail12306.model.Passenger 一一对应。枚举类型以 code（@JsonValue）序列化，
// 对应的中文名见下方 *_LABEL 映射（code 及 label 与后端枚举的 code/displayName 保持一致）。
export interface Passenger {
  isUser: boolean
  idType: number // IdType.code
  idNo: string
  name: string
  phone: string | null
  email: string | null
  countryCode: string | null
  birthDate: string | null
  sex: 'M' | 'F' | null // Sex.code
  validThrough: string | null
  discountType: number // DiscountType.code
  status: number // PassengerStatus.code
}

export const ID_TYPE_LABEL: Record<number, string> = {
  1: '中国居民身份证',
  2: '港澳居民居住证',
  3: '台湾居民居住证',
  4: '外国人永久居留身份证',
  5: '港澳居民来往内地通行证（含非中国籍）',
  6: '台湾居民来往大陆通行证',
  7: '中国护照',
  8: '外国护照',
}

export const DISCOUNT_TYPE_LABEL: Record<number, string> = {
  1: '成人',
  2: '儿童',
  3: '学生',
  4: '残疾军人',
}

export const PASSENGER_STATUS_LABEL: Record<number, string> = {
  1: '待核验',
  2: '已通过',
}

export const SEX_LABEL: Record<string, string> = {
  M: '男',
  F: '女',
}
