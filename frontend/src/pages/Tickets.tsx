import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import axios from 'axios'
import TicketSearch from '@/components/TicketSearch'
import TicketCard from '@/components/TicketCard'
import { Loader2, MoveRight } from 'lucide-react'
import { useStations } from '@/store/stations'
import type { Ticket } from '@/types'

function formatDate(d: string): string {
  if (!d) return ''
  const date = new Date(d + 'T00:00:00')
  const week = ['日', '一', '二', '三', '四', '五', '六']
  return `${date.getMonth() + 1}月${date.getDate()}日 星期${week[date.getDay()]}`
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
              {fromName} <span className="text-muted-foreground"><MoveRight className="inline" /></span> {toName}
            </span>
            <span className="text-sm text-muted-foreground ml-2.5">
              {formatDate(date)}
            </span>
            <span className="text-xs text-muted-foreground ml-auto">
              共计 <span className="text-primary font-semibold">{tickets.length}</span> 个车次
            </span>
          </div>

          <div className="space-y-2">
            {tickets.map(t => (
              <TicketCard key={t.trainCode} ticket={t} fromName={fromName} toName={toName} />
            ))}
          </div>
        </section>
      )}
    </div>
  )
}
