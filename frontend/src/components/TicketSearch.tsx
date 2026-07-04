import { useRef, useState } from 'react'
import { useStations } from '../store/stations'
import { Button } from '@/components/ui/button'

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
    () => stations[fromCode || ''] || fromCode || ''
  )
  const [toInput, setToInput] = useState(
    () => stations[toCode || ''] || toCode || ''
  )
  const [dateInput, setDateInput] = useState(() => date || '2026-07-05')

  const [from, setFrom] = useState(fromCode || '')
  const [to, setTo] = useState(toCode || '')

  const [fromFocus, setFromFocus] = useState(false)
  const [toFocus, setToFocus] = useState(false)
  const fromRef = useRef<HTMLDivElement>(null)
  const toRef = useRef<HTMLDivElement>(null)

  const suggestions = Object.entries(stations)  // [code, name][]

  function filterSuggestions(input: string) {
    return suggestions.filter(([, name]) => name.includes(input)).sort((a, b) => a[1].localeCompare(b[1], 'zh'))
  }

  const fromHints = filterSuggestions(fromInput)
  const toHints = filterSuggestions(toInput)

  function selectStation(which: 'from' | 'to', code: string, name: string) {
    if (which === 'from') {
      setFromInput(name)
      setFrom(code)
      setFromFocus(false)
    } else {
      setToInput(name)
      setTo(code)
      setToFocus(false)
    }
  }

  function handleFromChange(value: string) {
    setFromInput(value)
    setFromFocus(true)
    const m = resolveStation(value, stations)
    if (m) {
      setFrom(m[0])
      setFromInput(m[1])
    }
  }

  function handleToChange(value: string) {
    setToInput(value)
    setToFocus(true)
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
        <div ref={fromRef} className="relative">
          <label className="block text-xs font-semibold text-muted mb-1">出发站</label>
          <input
            type="text"
            value={fromInput}
            onChange={e => handleFromChange(e.target.value)}
            onFocus={() => setFromFocus(true)}
            onBlur={() => setTimeout(() => setFromFocus(false), 150)}
            placeholder="输入出发站"
            className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
          />
          {fromFocus && fromHints.length > 0 && (
            <ul className="absolute left-0 right-0 top-full mt-1 bg-card border border-stroke z-10 max-h-48 overflow-y-auto">
              {fromHints.map(([code, name]) => (
                <li
                  key={code}
                  onMouseDown={() => selectStation('from', code, name)}
                  className="pl-2.5 pr-5 py-1.5 text-sm text-ink cursor-pointer hover:bg-primary hover:text-white flex justify-between items-baseline"
                >
                  <span>{name}</span>
                  <span className="text-xs text-muted ml-3 tabular-nums">{code}</span>
                </li>
              ))}
            </ul>
          )}
        </div>

        <Button
          variant="outline"
          size="icon"
          onClick={swap}
          title="交换出发到达站"
        >
          ⇄
        </Button>

        <div ref={toRef} className="relative">
          <label className="block text-xs font-semibold text-muted mb-1">到达站</label>
          <input
            type="text"
            value={toInput}
            onChange={e => handleToChange(e.target.value)}
            onFocus={() => setToFocus(true)}
            onBlur={() => setTimeout(() => setToFocus(false), 150)}
            placeholder="输入到达站"
            className="w-full h-9 px-2.5 text-sm border border-stroke bg-card text-ink outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
          />
          {toFocus && toHints.length > 0 && (
            <ul className="absolute left-0 right-0 top-full mt-1 bg-card border border-stroke z-10 max-h-48 overflow-y-auto">
              {toHints.map(([code, name]) => (
                <li
                  key={code}
                  onMouseDown={() => selectStation('to', code, name)}
                  className="pl-2.5 pr-5 py-1.5 text-sm text-ink cursor-pointer hover:bg-primary hover:text-white flex justify-between items-baseline"
                >
                  <span>{name}</span>
                  <span className="text-xs text-muted ml-3 tabular-nums">{code}</span>
                </li>
              ))}
            </ul>
          )}
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

        <Button
          onClick={search}
        >
          查询
        </Button>
      </div>
    </section>
  )
}
