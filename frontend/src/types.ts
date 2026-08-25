export interface Seat {
  type: string
  price: number
  remaining: number
}

export interface Ticket {
  trainCode: string
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
