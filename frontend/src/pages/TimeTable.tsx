import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { format } from 'date-fns'
import axios from 'axios'
import type { TimeTableRow } from '@/types'
import { useStations } from '@/store/stations'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'

export default function TimeTable() {
  const [searchParams, setSearchParams] = useSearchParams()
  const stations = useStations(s => s.stations)

  const [inputCode, setInputCode] = useState('')
  const [rows, setRows] = useState<TimeTableRow[] | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const code = searchParams.get('code')
    if (code) {
      setInputCode(code)
      lookup(code)
    }
    // 仅挂载时读取 URL 参数，页面内查询走 lookup
  }, [])

  function lookup(query?: string) {
    const c = (query || inputCode).toUpperCase()
    if (!c) return
    const date = searchParams.get('date') || format(new Date(), 'yyyy-MM-dd')
    setSearchParams({ code: c, date }, { replace: true })
    setError('')
    setLoading(true)
    axios.get('/api/timetable', { params: { date, code: c } })
      .then(res => { setRows(res.data); setError('') })
      .catch(() => { setRows(null); setError('未找到该车次') })
      .finally(() => setLoading(false))
  }

  const stops = rows ?? []

  return (
    <div>
      <Card>
        <CardContent>
        <div className="flex gap-3 items-end">
          <div className="flex-1 min-w-0 flex flex-col gap-3">
            <Label>车次号</Label>
            <Input
              type="text"
              value={inputCode}
              onChange={e => setInputCode(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && lookup()}
              placeholder="例如: G1"
            />
          </div>
          <Button onClick={() => lookup()}>
            查询
          </Button>
        </div>
        </CardContent>
      </Card>

      {loading && (
        <div className="text-center py-12 text-muted-foreground">查询中...</div>
      )}

      {error && !loading && (
        <div className="text-center py-12 text-muted-foreground">{error}</div>
      )}

      {!loading && rows && (
        <Card>
          <CardContent>
          <div className="flex items-baseline gap-3 mb-4">
            <h2 className="text-xl font-extrabold text-foreground">{rows[0]?.trainCode}</h2>
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>停靠站</TableHead>
                <TableHead>到点</TableHead>
                <TableHead>开点</TableHead>
                <TableHead>停留</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {stops.map((s, i) => {
                const isFirst = i === 0
                const isLast = i === stops.length - 1
                const toPoint = isFirst ? '—' : (s.arriveTime ?? '—')
                const fromPoint = isLast ? '—' : (s.startTime ?? '—')
                const dwell = isFirst || isLast
                  ? '—'
                  : (s.arriveTime && s.startTime
                      ? (() => {
                          const [ah, am] = s.arriveTime.split(':').map(Number)
                          const [sh, sm] = s.startTime.split(':').map(Number)
                          return `${(sh * 60 + sm) - (ah * 60 + am)} 分`
                        })()
                      : '—')

                return (
                  <TableRow key={s.stationTelecode}>
                    <TableCell>
                      {stations[s.stationTelecode] || s.stationTelecode}
                    </TableCell>
                    <TableCell>{toPoint}</TableCell>
                    <TableCell>{fromPoint}</TableCell>
                    <TableCell>{dwell}</TableCell>
                  </TableRow>
                )
              })}
            </TableBody>
          </Table>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
