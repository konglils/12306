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

export interface Train {
  trainCodes: string
  style: string
  stations: {
    telecode: string
    trainCode: string
    arriveDay: number
    arriveTime: string
    startDay: number
    startTime: string
  }[]
}
