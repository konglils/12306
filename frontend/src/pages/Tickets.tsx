import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import axios from 'axios'
import TicketSearch from '../components/TicketSearch'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { useStations } from '../store/stations'
import { ChevronDown, ChevronUp, Loader2, MoveRight } from 'lucide-react'
import type { Ticket } from '../types'


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

export default function Tickets() {
  const [searchParams, setSearchParams] = useSearchParams()
  const stations = useStations(s => s.stations)

  const from = searchParams.get('from') || ''
  const to = searchParams.get('to') || ''
  const date = searchParams.get('date') || ''

  const [tickets, setTickets] = useState<Ticket[]>([])
  const [loading, setLoading] = useState(false)
  const [expanded, setExpanded] = useState<Set<string>>(new Set())

  useEffect(() => {
    if (!from || !to) return
    setExpanded(new Set())
    setLoading(true)
    axios.get('/api/tickets', { params: { from, to, date } })
      .then(res => setTickets(res.data))
      .catch(() => setTickets([]))
      .finally(() => setLoading(false))
  }, [from, to, date])

  function handleSearch(f: string, t: string, d: string) {
    setSearchParams({ from: f, to: t, date: d })
  }

  const fromName = stations[from] || from || '出发站'
  const toName = stations[to] || to || '到达站'
  const hasParams = from && to

  return (
    <div>
      <TicketSearch
        fromCode={from}
        toCode={to}
        date={date}
        onSearch={handleSearch}
      />

      {loading && (
        <div className="flex justify-center py-16">
          <Loader2 className="size-8 animate-spin text-muted-foreground" />
        </div>
      )}

      {!loading && hasParams && tickets.length === 0 && (
        <section className="mt-12 text-center">
          <p className="text-muted-foreground text-sm">未查询到符合条件的车次</p>
          <p className="text-muted-foreground text-xs mt-1">请尝试调整出发站、到达站或日期</p>
        </section>
      )}

      {!loading && hasParams && tickets.length > 0 && (
        <section className="mt-6">
          <div className="flex items-baseline mb-3">
            <span className="text-base font-bold text-foreground">
              {fromName} <span className="text-muted-foreground"><MoveRight className="inline"></MoveRight></span> {toName}
            </span>
            <span className="text-sm text-muted-foreground ml-2.5">
              {formatDate(date)}
            </span>
            <span className="text-xs text-muted-foreground ml-auto">
              共计 <span className="text-primary font-semibold">{tickets.length}</span> 个车次
            </span>
          </div>

          <div className="space-y-2">
            {tickets.map(t => {
              const isExpanded = expanded.has(t.trainCode)
              const minPrice = Math.min(...t.seats.map(s => s.price)) / 10
              return (
                <Card
                  key={t.trainCode}
                  className="px-4 py-3 select-none gap-0"
                >
                  <div
                    className="cursor-pointer"
                    onClick={() => {
                      const next = new Set(expanded)
                      if (isExpanded) next.delete(t.trainCode)
                      else next.add(t.trainCode)
                      setExpanded(next)
                    }}
                  >
                  <div className="flex justify-between">
                    <div className="flex-2 flex justify-between">
                      <div>
                        <div className="text-xl font-bold tracking-tight">{t.startTime}</div>
                        <div className="text-sm text-foreground mt-0.5">{fromName}</div>
                      </div>
                      <div className="text-center">
                        <div className="font-bold text-lg">{t.trainCode}</div>
                        <div className="text-xs text-foreground mt-0.5">
                          {calcDuration(t.startTime, t.arriveTime, t.arriveDay)}
                        </div>
                      </div>
                      <div className="text-center">
                        <div className="text-xl font-bold tracking-tight">{t.arriveTime}</div>
                        <div className="text-sm text-foreground mt-0.5">{toName}</div>
                      </div>
                    </div>
                    <div className="flex-1 flex flex-col items-end justify-center">
                      <div className="text-price font-bold text-lg">¥{formatPrice(minPrice)}起</div>
                      <div className="text-foreground/50 text-xs mt-1">
                        {isExpanded ? <ChevronUp className="size-4" /> : <ChevronDown className="size-4" />}
                      </div>
                    </div>
                  </div>

                  {/* 座位快捷状态 */}
                  {!isExpanded && (
                    <div className="flex flex-wrap gap-x-4 gap-y-1 mt-3 pt-3 border-t border-border">
                      {t.seats.map(s => (
                        <span key={s.type} className="text-sm text-foreground">
                          {s.type}{' '}
                          <span className={s.remaining > 0 ? 'text-success' : 'text-muted-foreground'}>
                            {s.remaining > 0 ? (s.remaining < 20 ? `${s.remaining}张` : '有票') : '售罄'}
                          </span>
                        </span>
                      ))}
                    </div>
                  )}
                  </div>

                  {/* 展开：座位价格详情 */}
                  {isExpanded && (
                    <div className="mt-3 pt-3 border-t border-border space-y-1">
                      {t.seats.map(s => (
                        <div
                          key={s.type}
                          className="flex items-center px-2 py-2 rounded-md hover:bg-muted/50 transition-colors"
                        >
                          <span className="w-16 text-sm font-medium text-foreground">{s.type}</span>
                          <span className="flex-1 text-price font-bold text-lg tabular-nums">
                            ¥{formatPrice(s.price / 10)}
                          </span>
                          <span className={`text-sm w-16 text-center ${s.remaining > 0 ? 'text-success' : 'text-muted-foreground'}`}>
                            {s.remaining > 0 ? '有票' : '售罄'}
                          </span>
                          <Button
                            variant="outline"
                            size="sm"
                            className="ml-4 w-14"
                            disabled={s.remaining === 0}
                            onClick={e => { e.stopPropagation(); /* 预留预订逻辑 */ }}
                          >
                            预订
                          </Button>
                        </div>
                      ))}
                    </div>
                  )}
                </Card>
              )
            })}
          </div>
        </section>
      )}
    </div>
  )
}
