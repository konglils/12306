import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import axios from 'axios'
import TicketSearch from '../components/TicketSearch'
import { Card, CardContent } from '@/components/ui/card'
import { useStations } from '../store/stations'
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

export default function Tickets() {
  const [searchParams, setSearchParams] = useSearchParams()
  const stations = useStations(s => s.stations)

  const from = searchParams.get('from') || ''
  const to = searchParams.get('to') || ''
  const date = searchParams.get('date') || ''

  const [tickets, setTickets] = useState<Ticket[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!from || !to) return
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
  const gridCols = 1 + Math.max(...tickets.map(t => t.seats.length), 0)

  return (
    <div>
      <TicketSearch
        fromCode={from}
        toCode={to}
        date={date}
        onSearch={handleSearch}
      />

      {loading && (
        <div className="text-center py-12 text-muted-foreground">查询中...</div>
      )}

      {!loading && hasParams && (
        <section>
          <div className="flex items-baseline mb-3">
            <span className="text-base font-bold text-foreground">
              {fromName} <span className="text-muted-foreground mx-[0.3em]">—</span> {toName}
            </span>
            <span className="text-sm text-muted-foreground ml-2.5">
              {formatDate(date)}
            </span>
            <span className="text-xs text-muted-foreground ml-auto">
              共计 <span className="text-primary font-semibold">{tickets.length}</span> 个车次
            </span>
          </div>

          {tickets.map(t => (
            <Card key={t.trainCode}>
              <CardContent>
                <div
                  className="grid gap-x-10 gap-y-1"
                  style={{ gridTemplateColumns: `repeat(${gridCols}, 1fr)` }}
                >
                  <div className="font-bold text-base">
                    <Link to={`/trains?code=${t.trainCode}`} className="hover:underline">{t.trainCode}</Link>
                  </div>
                  {t.seats.map(s => (
                    <div key={s.type} className="text-muted-foreground">{s.type}</div>
                  ))}
                  {Array.from({ length: gridCols - 1 - t.seats.length }).map((_, i) => (
                    <div key={`st-${i}`} />
                  ))}
                  <div className="text-muted-foreground">
                    {t.startTime} — {t.arriveTime}
                    {t.arriveDay > 0 && ` (+${t.arriveDay})`}
                  </div>
                  {t.seats.map(s => (
                    <div key={s.type} className="font-bold">¥{formatPrice(s.price / 10)}</div>
                  ))}
                  {Array.from({ length: gridCols - 1 - t.seats.length }).map((_, i) => (
                    <div key={`pr-${i}`} />
                  ))}
                </div>
              </CardContent>
            </Card>
          ))}
        </section>
      )}
    </div>
  )
}
