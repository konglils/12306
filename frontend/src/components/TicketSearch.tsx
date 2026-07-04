import { useState } from 'react'
import { Check, ChevronsUpDown } from 'lucide-react'
import { useStations } from '../store/stations'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent } from '@/components/ui/card'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Command, CommandInput, CommandList, CommandEmpty, CommandGroup, CommandItem, CommandShortcut } from '@/components/ui/command'

interface Props {
  fromCode?: string
  toCode?: string
  date?: string
  onSearch: (from: string, to: string, date: string) => void
}

export default function TicketSearch({ fromCode, toCode, date, onSearch }: Props) {
  const stations = useStations(s => s.stations)

  const [from, setFrom] = useState(fromCode || '')
  const [to, setTo] = useState(toCode || '')
  const [dateInput, setDateInput] = useState(() => date || '2026-07-05')

  const [fromOpen, setFromOpen] = useState(false)
  const [toOpen, setToOpen] = useState(false)

  const stationList = Object.entries(stations).sort((a, b) => a[1].localeCompare(b[1], 'zh'))

  function swap() {
    const tmp = from
    setFrom(to)
    setTo(tmp)
  }

  function search() {
    onSearch(from, to, dateInput)
  }

  return (
    <Card>
      <CardContent>
      <div className="grid gap-2.5 items-end" style={{ gridTemplateColumns: '1fr auto 1fr 0.7fr auto' }}>
        <div>
          <Label>出发站</Label>
          <Popover open={fromOpen} onOpenChange={setFromOpen}>
            <PopoverTrigger asChild>
              <Button variant="outline" role="combobox" className="w-full justify-between">
                {from ? stations[from] : '选择出发站'}
                <ChevronsUpDown className="size-4 opacity-50" />
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[--radix-popover-trigger-width] p-0">
              <Command>
                <CommandInput placeholder="搜索车站..." />
                <CommandList>
                  <CommandEmpty>未找到车站</CommandEmpty>
                  <CommandGroup>
                    {stationList.map(([code, name]) => (
                      <CommandItem
                        key={code}
                        value={name}
                        onSelect={() => {
                          setFrom(code)
                          setFromOpen(false)
                        }}
                      >
                        {from === code && <Check className="size-4 shrink-0" />}
                        {name}
                        <CommandShortcut>{code}</CommandShortcut>
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover>
        </div>

        <Button variant="outline" size="icon" onClick={swap} title="交换出发到达站">
          ⇄
        </Button>

        <div>
          <Label>到达站</Label>
          <Popover open={toOpen} onOpenChange={setToOpen}>
            <PopoverTrigger asChild>
              <Button variant="outline" role="combobox" className="w-full justify-between">
                {to ? stations[to] : '选择到达站'}
                <ChevronsUpDown className="size-4 opacity-50" />
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[--radix-popover-trigger-width] p-0">
              <Command>
                <CommandInput placeholder="搜索车站..." />
                <CommandList>
                  <CommandEmpty>未找到车站</CommandEmpty>
                  <CommandGroup>
                    {stationList.map(([code, name]) => (
                      <CommandItem
                        key={code}
                        value={name}
                        onSelect={() => {
                          setTo(code)
                          setToOpen(false)
                        }}
                      >
                        {to === code && <Check className="size-4 shrink-0" />}
                        {name}
                        <CommandShortcut>{code}</CommandShortcut>
                      </CommandItem>
                    ))}
                  </CommandGroup>
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover>
        </div>

        <div>
          <Label>出发日期</Label>
          <Input
            type="date"
            value={dateInput}
            onChange={e => setDateInput(e.target.value)}
          />
        </div>

        <Button onClick={search}>查询</Button>
      </div>
      </CardContent>
    </Card>
  )
}
