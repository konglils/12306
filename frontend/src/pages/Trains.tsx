import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import axios from 'axios'
import type { Train } from '../types'
import { useStations } from '../store/stations'

export default function Trains() {
  const [searchParams, setSearchParams] = useSearchParams()
  const stations = useStations(s => s.stations)

  const [inputCode, setInputCode] = useState('')
  const [train, setTrain] = useState<Train | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const code = searchParams.get('code')
    if (code) {
      setInputCode(code)
      lookup(code)
    }
  }, [])

  function lookup(query?: string) {
    const c = (query || inputCode).toUpperCase()
    if (!c) return
    setSearchParams({ code: c }, { replace: true })
    setError('')
    setLoading(true)
    axios.get('/api/trains', { params: { code: c } })
      .then(res => { setTrain(res.data); setError('') })
      .catch(() => { setTrain(null); setError('未找到该车次') })
      .finally(() => setLoading(false))
  }

  const stops = train?.stations ?? []

  return (
    <div>
      <section className="bg-card border border-stroke px-6 py-5 mb-5">
        <div className="flex gap-2.5 items-end">
          <div className="flex-1 min-w-0">
            <label className="block text-xs font-semibold text-muted mb-1">车次号</label>
            <input
              type="text"
              value={inputCode}
              onChange={e => setInputCode(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && lookup()}
              placeholder="例如: G40"
              className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
            />
          </div>
          <button
            type="button"
            onClick={() => lookup()}
            className="h-9 px-6 border border-primary bg-primary text-white font-bold text-sm cursor-pointer whitespace-nowrap hover:bg-primary-hi hover:border-primary-hi"
          >
            查 询
          </button>
        </div>
      </section>

      {loading && (
        <div className="text-center py-12 text-muted">查询中...</div>
      )}

      {error && !loading && (
        <div className="text-center py-12 text-muted">{error}</div>
      )}

      {!loading && train && (
        <section className="bg-card border border-stroke p-6">
          <div className="flex items-baseline gap-3 mb-4">
            <h2 className="text-xl font-extrabold text-ink">{train.trainCodes}</h2>
            <span className="text-sm text-muted">{train.style}</span>
          </div>

          <table className="w-full table-fixed">
            <thead>
              <tr className="text-xs text-muted">
                <th className="font-normal pb-2 text-center align-middle">停靠站</th>
                <th className="font-normal pb-2 text-center align-middle">到点</th>
                <th className="font-normal pb-2 text-center align-middle">开点</th>
                <th className="font-normal pb-2 text-center align-middle">停留</th>
              </tr>
            </thead>
            <tbody>
              {stops.map((s, i) => {
                const isFirst = i === 0
                const isLast = i === stops.length - 1
                const toPoint = isFirst ? '—' : s.arriveTime
                const fromPoint = isLast ? '—' : s.startTime
                const dwell = isFirst || isLast
                  ? '—'
                  : (() => {
                      const [ah, am] = s.arriveTime.split(':').map(Number)
                      const [sh, sm] = s.startTime.split(':').map(Number)
                      return `${(sh * 60 + sm) - (ah * 60 + am)} 分`
                    })()

                return (
                  <tr key={s.telecode} className="border-t border-sep first:border-t-0">
                    <td className="relative py-2.5 border-l-2 border-primary text-sm text-ink text-center align-middle
                      before:absolute before:-left-[5px] before:top-[calc(50%-4px)] before:w-2 before:h-2 before:bg-primary">
                      {stations[s.telecode] || s.telecode}
                    </td>
                    <td className="py-2.5 text-sm text-ink text-center align-middle">{toPoint}</td>
                    <td className="py-2.5 text-sm text-ink text-center align-middle">{fromPoint}</td>
                    <td className="py-2.5 text-sm text-muted text-center align-middle">{dwell}</td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </section>
      )}
    </div>
  )
}
