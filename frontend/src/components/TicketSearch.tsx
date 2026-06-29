import { useState } from 'react'
import { useStations } from '../store/stations'

interface Props {
  fromCode?: string
  toCode?: string
  date?: string
  onSearch: (from: string, to: string, date: string) => void
}

function resolveStation(input: string, dict: Record<string, string>): [string, string] | null {
  for (const [code, name] of Object.entries(dict)) {
    if (name === input || code === input.toUpperCase()) return [code, name]
  }
  return null
}

export default function TicketSearch({ fromCode, toCode, date, onSearch }: Props) {
  const stations = useStations(s => s.stations)

  const [fromInput, setFromInput] = useState(
    () => stations[fromCode || ''] || fromCode || '北京南'
  )
  const [toInput, setToInput] = useState(
    () => stations[toCode || ''] || toCode || '上海虹桥'
  )
  const [dateInput, setDateInput] = useState(() => date || '2026-07-05')

  const [from, setFrom] = useState(fromCode || '')
  const [to, setTo] = useState(toCode || '')

  function handleFromChange(value: string) {
    setFromInput(value)
    const m = resolveStation(value, stations)
    if (m) {
      setFrom(m[0])
      setFromInput(m[1])
    }
  }

  function handleToChange(value: string) {
    setToInput(value)
    const m = resolveStation(value, stations)
    if (m) {
      setTo(m[0])
      setToInput(m[1])
    }
  }

  function swap() {
    const tmpInput = fromInput
    const tmpFrom = from
    setFromInput(toInput)
    setFrom(to)
    setToInput(tmpInput)
    setTo(tmpFrom)
  }

  function search() {
    const f = fromInput && resolveStation(fromInput, stations)
    const t = toInput && resolveStation(toInput, stations)
    const newFrom = f ? f[0] : from
    const newTo = t ? t[0] : to
    if (f) setFromInput(f[1])
    if (t) setToInput(t[1])
    setFrom(newFrom)
    setTo(newTo)
    onSearch(newFrom, newTo, dateInput)
  }

  return (
    <section className="bg-card border border-stroke px-6 py-5 mb-5">
      <div className="grid gap-2.5 items-end" style={{ gridTemplateColumns: '1fr auto 1fr 0.7fr auto' }}>
        <div>
          <label className="block text-xs font-semibold text-muted mb-1">出发站</label>
          <input
            type="text"
            value={fromInput}
            onChange={e => handleFromChange(e.target.value)}
            placeholder="输入出发站"
            className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
          />
        </div>

        <button
          type="button"
          onClick={swap}
          className="w-9 h-9 border border-stroke bg-card text-primary cursor-pointer flex items-center justify-center shrink-0 hover:bg-primary hover:text-white hover:border-primary"
          title="交换出发到达站"
        >
          ⇄
        </button>

        <div>
          <label className="block text-xs font-semibold text-muted mb-1">到达站</label>
          <input
            type="text"
            value={toInput}
            onChange={e => handleToChange(e.target.value)}
            placeholder="输入到达站"
            className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-muted mb-1">出发日期</label>
          <input
            type="date"
            value={dateInput}
            onChange={e => setDateInput(e.target.value)}
            className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
          />
        </div>

        <button
          type="button"
          onClick={search}
          className="h-9 px-6 border border-primary bg-primary text-white font-bold text-sm cursor-pointer whitespace-nowrap hover:bg-primary-hi hover:border-primary-hi"
        >
          查 询
        </button>
      </div>
    </section>
  )
}
