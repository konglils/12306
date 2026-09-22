import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useSearchParams } from 'react-router-dom'
import axios from 'axios'
import { Circle, CircleCheck, Loader2, Trash2, UserPlus } from 'lucide-react'
import type { Passenger, Seat, SeatTypeCode, Ticket } from '@/types'
import { DISCOUNT_TYPE_LABEL, SEAT_TYPE_LABEL } from '@/types'
import { useAuth } from '@/store/auth'
import { useStations } from '@/store/stations'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { PassengerPickerDialog } from '@/components/PassengerPickerDialog'
import { BerthSelector, SeatSelector } from '@/components/SeatService'
import { getSeatService, type BerthLevel } from '@/lib/seatService'

function formatDate(d: string): string {
  if (!d) return ''
  const date = new Date(d + 'T00:00:00')
  const week = ['日', '一', '二', '三', '四', '五', '六']
  return `${date.getMonth() + 1}月${date.getDate()}日 星期${week[date.getDay()]}`
}

function formatPrice(price: number): string {
  return String(price).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function calcDuration(start: string, arrive: string, arriveDay: number): string {
  const [sh, sm] = start.split(':').map(Number)
  const [eh, em] = arrive.split(':').map(Number)
  let min = eh * 60 + em - (sh * 60 + sm) + arriveDay * 1440
  if (min < 0) min += 1440
  const h = Math.floor(min / 60)
  const m = min % 60
  return `${h}时${m > 0 ? m + '分' : ''}`
}

// 无座（hasSeat=false）统一显示“无座”，其余按席别编码映射中文名
function seatLabel(s: Seat): string {
  return s.hasSeat ? (SEAT_TYPE_LABEL[s.type] ?? s.type) : '无座'
}

// 余票文案：少量显示具体张数，充足显示“有票”，无票显示“售罄”
function remainingText(s: Seat): string {
  if (s.remaining <= 0) return '售罄'
  return s.remaining < 20 ? `${s.remaining}张` : '有票'
}

// 乘车人的唯一 key，与删除接口的定位参数一致；编辑不会改动这两个字段
function passengerKey(p: Passenger): string {
  return `${p.idType}-${p.idNo}`
}

export default function Order() {
  const [searchParams] = useSearchParams()
  const location = useLocation()

  const date = searchParams.get('date') || ''
  const trainId = searchParams.get('trainId') || ''
  // from/to 是城市区域 id
  const from = searchParams.get('from') || ''
  const to = searchParams.get('to') || ''

  const username = useAuth(s => s.username)
  const check = useAuth(s => s.check)
  const stations = useStations(s => s.stations)

  // 预订跳转时通过路由 state 携带的车票数据；直接刷新页面时为空，走兜底请求
  const stateTicket = (location.state as { ticket?: Ticket } | null)?.ticket ?? null

  const [authLoaded, setAuthLoaded] = useState(false)
  const [ticket, setTicket] = useState<Ticket | null>(stateTicket)
  const [ticketError, setTicketError] = useState('')
  const ticketLoading = !stateTicket && !ticketError

  const [passengers, setPassengers] = useState<Passenger[]>([])
  const [loadingPassengers, setLoadingPassengers] = useState(false)
  const [passengersError, setPassengersError] = useState('')

  // 已勾选乘车人 key 集合：勾选状态在页面持有，dialog 关闭再打开仍保持
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set())
  const [seatType, setSeatType] = useState<SeatTypeCode | null>(null)
  // 本次下单使用的优惠类型（key -> code），不回写数据库
  const [discountOverrides, setDiscountOverrides] = useState<Record<string, number>>({})
  const [pickerOpen, setPickerOpen] = useState(false)
  // 选座状态：固定 2 行 × 5 列（A B C D F），所有席别的选座都用这个数组
  const [seatSelection, setSeatSelection] = useState<boolean[][]>([
    [false, false, false, false, false],
    [false, false, false, false, false],
  ])
  // 选铺状态：各铺位票数
  const [berthCounts, setBerthCounts] = useState<Record<BerthLevel, number>>({
    lower: 0,
    middle: 0,
    upper: 0,
  })

  useEffect(() => {
    check().finally(() => setAuthLoaded(true))
  }, [check])

  // 兜底：路由 state 丢失（如刷新）时，仅重新请求这一个车次
  useEffect(() => {
    if (stateTicket || !date || !trainId || !from || !to) return
    axios.get('/api/tickets', { params: { date, from, to, trainId } })
      .then(res => {
        const list = res.data as Ticket[]
        if (list.length === 0) {
          setTicketError('未找到该车次，请返回车票列表重新选择')
          return
        }
        setTicket(list[0])
      })
      .catch(err => {
        if (axios.isAxiosError(err) && err.response) {
          setTicketError(err.response.data.message || '查询车次信息失败，请稍后重试')
        } else {
          setTicketError('网络错误，请稍后重试')
        }
      })
  }, [stateTicket, date, trainId, from, to])

  const loadPassengers = useCallback(() => {
    setLoadingPassengers(true)
    setPassengersError('')
    axios.get('/api/passengers')
      .then(res => setPassengers((res.data as Passenger[]).map(p => ({ ...p, name: p.name.trim() }))))
      .catch(() => setPassengersError('获取乘车人列表失败，请稍后重试'))
      .finally(() => setLoadingPassengers(false))
  }, [])

  // 页面加载时即请求乘车人列表并保存
  useEffect(() => {
    if (!username) return
    loadPassengers()
  }, [username, loadPassengers])

  // 座位排序与查票卡片一致：有座在前、无座在后，各自组内按价格从低到高
  const seats = useMemo(() => {
    if (!ticket) return []
    return [...ticket.seats].sort((a, b) => {
      if (a.hasSeat !== b.hasSeat) return a.hasSeat ? -1 : 1
      return a.price - b.price
    })
  }, [ticket])

  // 默认选中最便宜的有票席别
  useEffect(() => {
    if (!ticket || seatType !== null) return
    const available = seats.filter(s => s.remaining > 0)
    if (available.length === 0) return
    setSeatType(available[0].type)
  }, [ticket, seats, seatType])

  // 切换席别时重置已选的座位/铺位
  useEffect(() => {
    setSeatSelection([
      [false, false, false, false, false],
      [false, false, false, false, false],
    ])
    setBerthCounts({ lower: 0, middle: 0, upper: 0 })
  }, [seatType])

  function handleToggle(key: string, checked: boolean) {
    setSelectedIds(prev => {
      const next = new Set(prev)
      if (checked) next.add(key)
      else next.delete(key)
      return next
    })
  }

  if (!authLoaded) return null

  if (!username) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center gap-3 py-10 text-center">
          <p className="text-muted-foreground">登录后即可预订车票。</p>
          <Button asChild>
            <Link to="/signin">去登录</Link>
          </Button>
        </CardContent>
      </Card>
    )
  }

  const fromName = ticket ? (stations[ticket.fromTelecode] ?? ticket.fromTelecode) : from
  const toName = ticket ? (stations[ticket.toTelecode] ?? ticket.toTelecode) : to
  const duration = ticket ? calcDuration(ticket.startTime, ticket.arriveTime, ticket.arriveDay) : ''

  // 已勾选的乘车人条目（按乘车人列表顺序）
  const selectedPassengers = passengers.filter(p => selectedIds.has(passengerKey(p)))

  // 选座/选铺服务：无座（hasSeat=false）与无服务席别不显示；未选乘车人时不显示
  const selectedSeat = seatType ? ticket?.seats.find(s => s.type === seatType) : undefined
  const service = selectedSeat?.hasSeat && seatType ? getSeatService(seatType) : null
  const showService = service !== null && selectedPassengers.length >= 1
  // 选座服务最多支持 5 位乘车人，超出时不显示选座、仅提示；选铺服务不受限制
  const seatUnavailable = service?.kind === 'seat' && selectedPassengers.length > 5

  return (
    <div>
      {ticketLoading && (
        <div className="flex justify-center py-16">
          <Loader2 className="size-8 animate-spin text-muted-foreground" />
        </div>
      )}

      {ticketError && (
        <section className="text-center py-16">
          <p className="text-muted-foreground text-sm">{ticketError}</p>
          <Button variant="outline" asChild className="mt-4">
            <Link to="/tickets">返回车票列表</Link>
          </Button>
        </section>
      )}

      {ticket && (
        <>
          {/* 车次信息卡片：与查票结果卡片头部一致 */}
          <div className="flex items-baseline mb-3">
            <span className="text-sm text-muted-foreground">
              {formatDate(date)}
            </span>
          </div>
          <Card className="px-4 py-3 mb-8 gap-0">
            <div className="flex-2 flex justify-between">
              <div>
                <div className="text-xl font-bold tracking-tight">{ticket.startTime}</div>
                <div className="text-sm text-foreground mt-0.5">{fromName}</div>
              </div>
              <Link
                to={`/timetable?code=${encodeURIComponent(ticket.trainCode)}&date=${encodeURIComponent(date)}`}
                className="text-center no-underline hover:underline underline-offset-2"
              >
                <div className="font-bold text-lg text-foreground">{ticket.trainCode}</div>
                <div className="text-xs text-muted-foreground mt-0.5 underline-offset-2">
                  {duration}
                </div>
              </Link>
              <div className="text-center">
                <div className="text-xl font-bold tracking-tight">{ticket.arriveTime}</div>
                <div className="text-sm text-foreground mt-0.5">{toName}</div>
              </div>
            </div>
          </Card>

          <section className="mb-8">
            <h1 className="mb-3 text-lg font-bold">选择席别</h1>
            <Card className="gap-0 px-4 py-2">
              {seats.map(s => {
                const selectable = s.remaining > 0
                const active = seatType === s.type
                return (
                  <button
                    type="button"
                    key={`${s.type}-${s.hasSeat}`}
                    disabled={!selectable}
                    onClick={() => setSeatType(s.type)}
                    className="flex w-full items-center px-2 py-2 rounded-md text-left transition-colors hover:bg-muted/50 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    {active ? (
                      <CircleCheck className="size-4 text-primary" />
                    ) : (
                      <Circle className="size-4 text-muted-foreground" />
                    )}
                    <span className="ml-3 w-16 text-sm font-medium text-foreground">{seatLabel(s)}</span>
                    <span className="flex-1 text-price font-bold text-lg tabular-nums">
                      ¥{formatPrice(s.price / 10)}
                    </span>
                    <span className={`text-sm w-16 text-center ${s.remaining > 0 ? 'text-success' : 'text-muted-foreground'}`}>
                      {remainingText(s)}
                    </span>
                  </button>
                )
              })}
            </Card>
          </section>

          <section>
            <div className="flex items-center justify-between mb-3">
              <h1 className="text-lg font-bold">乘车人</h1>
              <Button type="button" onClick={() => setPickerOpen(true)}>
                <UserPlus data-icon="inline-start" />
                添加乘车人
              </Button>
            </div>

            {selectedPassengers.length === 0 && (
              <p className="text-sm text-muted-foreground py-6 text-center">
                尚未选择乘车人，点击“添加乘车人”进行选择
              </p>
            )}

            {selectedPassengers.length > 0 && (
              <div className="space-y-2">
                {selectedPassengers.map(p => {
                  const key = passengerKey(p)
                  // 已通过(status===2)绿色，未通过(status===3)红色，待核验(status===1)灰色
                  const nameColor = p.status === 2 ? 'text-success' : p.status === 3 ? 'text-destructive' : 'text-muted-foreground'
                  return (
                    <Card key={key}>
                      <CardContent>
                        <div className="flex items-center gap-2">
                          <div className="min-w-0 flex-1">
                            <div className="flex items-center gap-2">
                              <span className={`text-base font-semibold ${nameColor}`}>{p.name}</span>
                              {/* 本次下单使用的优惠类型，仅本地生效 */}
                              <Select
                                value={String(discountOverrides[key] ?? p.discountType)}
                                onValueChange={v => setDiscountOverrides(prev => ({ ...prev, [key]: Number(v) }))}
                              >
                                <SelectTrigger size="sm" aria-label={`${p.name} 的优惠类型`}>
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                  {Object.entries(DISCOUNT_TYPE_LABEL).map(([code, label]) => (
                                    <SelectItem key={code} value={code}>{label}</SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                            </div>
                            <p className="mt-1 text-sm text-muted-foreground">{p.idNo}</p>
                          </div>
                          <Button
                            type="button"
                            variant="ghost"
                            size="icon-sm"
                            onClick={() => handleToggle(key, false)}
                            aria-label={`删除乘车人 ${p.name}`}
                          >
                            <Trash2 />
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  )
                })}
              </div>
            )}
          </section>

          {showService && service && (
            <section className="mt-8">
              <h1 className="mb-3 text-lg font-bold">
                {service.kind === 'seat' ? '选座服务' : '选铺服务'}
              </h1>
              {seatUnavailable ? (
                <p className="text-xs text-muted-foreground">
                  超过5位乘车人时，暂不支持选座服务。
                </p>
              ) : (
                <>
                  <Card className="gap-0 px-4 py-3">
                    {service.kind === 'seat' ? (
                      <SeatSelector
                        letters={service.letters}
                        count={selectedPassengers.length}
                        value={seatSelection}
                        onChange={setSeatSelection}
                      />
                    ) : (
                      <BerthSelector
                        levels={service.levels}
                        count={selectedPassengers.length}
                        value={berthCounts}
                        onChange={setBerthCounts}
                      />
                    )}
                  </Card>
                  <p className="mt-3 text-xs text-muted-foreground">
                    {service.kind === 'seat'
                      ? '若剩余席位无法满足您的需求，系统将自动为您分配席位。'
                      : '如剩余铺位无法满足您的需求，系统将自动为您分配。'}
                  </p>
                </>
              )}
            </section>
          )}

          {/* 提交订单：暂未接入下单接口，占位 */}
          <section className="mt-8 flex justify-end">
            <Button size="lg">提交订单</Button>
          </section>
        </>
      )}

      <PassengerPickerDialog
        open={pickerOpen}
        onOpenChange={setPickerOpen}
        passengers={passengers}
        loading={loadingPassengers}
        error={passengersError}
        selectedIds={selectedIds}
        onToggle={handleToggle}
        onReload={loadPassengers}
        onConfirm={() => {}}
      />
    </div>
  )
}
