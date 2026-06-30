import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import axios from 'axios'
import TicketSearch from '../components/TicketSearch'
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

  return (
    <div>
      <TicketSearch
        fromCode={from}
        toCode={to}
        date={date}
        onSearch={handleSearch}
      />

      {loading && (
        <div className="text-center py-12 text-muted">查询中...</div>
      )}

      {!loading && hasParams && (
        <section>
          <div className="flex items-baseline mb-3">
            <span className="text-base font-bold text-ink">
              {fromName} <span className="text-gold mx-[0.3em]">—</span> {toName}
            </span>
            <span className="text-sm text-muted ml-2.5">
              {formatDate(date)}
            </span>
            <span className="text-xs text-muted ml-auto">
              共计 <span className="text-primary font-semibold">{tickets.length}</span> 个车次
            </span>
          </div>

          {tickets.map(t => (
            <article key={t.trainCode} className="bg-card border border-stroke mb-3 last:mb-0">
              <div
                className="grid items-center gap-x-3 px-4 py-2.5 bg-primary text-white"
                style={{ gridTemplateColumns: '4rem 1fr auto' }}
              >
                <span className="text-lg font-extrabold">{t.trainCode}</span>
                <span className="text-base font-semibold">
                  {t.startTime} — {t.arriveTime}
                  {t.arriveDay > 0 && (
                    <sup className="text-sm text-gold">+{t.arriveDay}</sup>
                  )}
                </span>
              </div>

              <div
                className="px-4 py-2 grid gap-x-16 gap-y-2"
                style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))' }}
              >
                {t.seats.map(s => (
                  <div key={s.type} className="flex justify-between items-baseline border-t border-dotted border-sep first:border-t-0">
                    <span className="text-xs text-muted">{s.type}</span>
                    <span className="font-bold text-base text-ink">
                      <span className="text-xs mr-px">¥</span>
                      {formatPrice(s.price / 10)}
                    </span>
                  </div>
                ))}
              </div>
            </article>
          ))}
        </section>
      )}
    </div>
  )
}
