import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import { ChevronDown } from 'lucide-react'
import { pinyin } from 'pinyin-pro'
import type { Passenger } from '@/types'
import {
  DISCOUNT_TYPE_LABEL,
  ID_TYPE_LABEL,
  PASSENGER_STATUS_LABEL,
  SEX_LABEL,
} from '@/types'
import { useAuth } from '@/store/auth'
import { cn } from '@/lib/utils'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { AddPassengerDialog } from '@/components/AddPassengerDialog'

function InfoBox({ label, value }: { label: string; value: string | null | undefined }) {
  return (
    <div className="rounded-md bg-muted px-3 py-2">
      <div className="text-xs text-muted-foreground">{label}</div>
      <div className="mt-0.5 text-sm text-muted-foreground">{value || '—'}</div>
    </div>
  )
}

// 取姓名首个字符的拼音首字母（大写）；非中文字符原样透传，非 A-Z 归入 '#' 组。
// 先去除首尾空白，避免姓名带空格被误判进 '#' 组。
function initialOf(name: string): string {
  const [first] = pinyin(name.trim(), { pattern: 'first', toneType: 'none', type: 'array' })
  const c = (first ?? '').trim().toUpperCase()
  return /^[A-Z]$/.test(c) ? c : '#'
}

export default function Passengers() {
  const username = useAuth(s => s.username)
  const check = useAuth(s => s.check)

  const [loaded, setLoaded] = useState(false)
  const [passengers, setPassengers] = useState<Passenger[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [expandedKey, setExpandedKey] = useState<string | null>(null)

  useEffect(() => {
    check().finally(() => setLoaded(true))
  }, [check])

  const loadPassengers = useCallback(() => {
    setLoading(true)
    setError('')
    axios.get('/api/passengers')
      .then(res => setPassengers((res.data as Passenger[]).map(p => ({ ...p, name: p.name.trim() }))))
      .catch(() => setError('获取乘车人列表失败，请稍后重试'))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    if (!username) return
    loadPassengers()
  }, [username, loadPassengers])

  // isUser 为 true 的乘车人排在最上面
  const main = useMemo(() => passengers.filter(p => p.isUser), [passengers])

  // 其余乘车人按姓名拼音首字母分组（如 张三 -> Z 组），组内按姓名排序
  const otherGroups = useMemo(() => {
    const groups = new Map<string, Passenger[]>()
    for (const p of passengers) {
      if (p.isUser) continue
      const key = initialOf(p.name)
      const list = groups.get(key)
      if (list) list.push(p)
      else groups.set(key, [p])
    }
    const entries = [...groups.entries()]
    // 字母组按 A-Z 排列，非字母（'#'）排在最后
    entries.sort((a, b) => {
      const aHash = a[0] === '#'
      const bHash = b[0] === '#'
      if (aHash !== bHash) return aHash ? 1 : -1
      return a[0].localeCompare(b[0])
    })
    for (const [, list] of entries) {
      list.sort((a, b) => a.name.localeCompare(b.name, 'zh-Hans-CN'))
    }
    return entries
  }, [passengers])

  if (!loaded) return null

  if (!username) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center gap-3 py-10 text-center">
          <p className="text-muted-foreground">登录后即可查看乘车人信息。</p>
          <Button asChild>
            <Link to="/signin">去登录</Link>
          </Button>
        </CardContent>
      </Card>
    )
  }

  function renderCard(p: Passenger) {
    const key = `${p.idType}-${p.idNo}`
    const expanded = expandedKey === key
    return (
      <Card key={key}>
        <CardContent>
          <button
            type="button"
            onClick={() => setExpandedKey(expanded ? null : key)}
            className={cn(
              'flex w-full items-center justify-between gap-3 text-left',
              'aria-expanded cursor-pointer',
            )}
            aria-expanded={expanded}
          >
            <div className="min-w-0">
              <div className="flex items-center gap-2">
                <span className="text-base font-semibold">{p.name}</span>
                <span className="rounded bg-muted px-1.5 py-0.5 text-xs text-muted-foreground">
                  {DISCOUNT_TYPE_LABEL[p.discountType] ?? '其他'}
                </span>
              </div>
              <p className="mt-1 text-sm text-muted-foreground">{p.idNo}</p>
            </div>
            <ChevronDown
              className={cn(
                'size-4 shrink-0 text-muted-foreground transition-transform',
                expanded && 'rotate-180',
              )}
            />
          </button>

          {expanded && (
            <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
              <InfoBox label="证件类型" value={ID_TYPE_LABEL[p.idType]} />
              <InfoBox label="性别" value={SEX_LABEL[p.sex ?? '']} />
              <InfoBox label="出生日期" value={p.birthDate} />
              <InfoBox label="手机号" value={p.phone} />
              <InfoBox label="邮箱" value={p.email} />
              <InfoBox label="证件有效期至" value={p.validThrough} />
              <InfoBox label="国籍/地区" value={p.countryCode} />
              <InfoBox label="核验状态" value={PASSENGER_STATUS_LABEL[p.status]} />
            </div>
          )}
        </CardContent>
      </Card>
    )
  }

  return (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-xl font-bold">乘车人</h1>
        <AddPassengerDialog onAdded={loadPassengers} />
      </div>

      {loading && (
        <div className="text-center py-12 text-muted-foreground">加载中...</div>
      )}

      {error && !loading && (
        <div className="text-center py-12 text-muted-foreground">{error}</div>
      )}

      {!loading && !error && passengers.length === 0 && (
        <div className="text-center py-12 text-muted-foreground">暂无乘车人</div>
      )}

      {!loading && !error && passengers.length > 0 && (
        <div className="flex flex-col gap-6">
          {main.length > 0 && (
            <section>
              <h2 className="mb-3 text-lg font-bold">当前用户</h2>
              <div className="flex flex-col gap-3">{main.map(renderCard)}</div>
            </section>
          )}
          {otherGroups.map(([letter, list]) => (
            <section key={letter}>
              <h2 className="mb-3 text-lg font-bold">{letter}</h2>
              <div className="flex flex-col gap-3">{list.map(renderCard)}</div>
            </section>
          ))}
        </div>
      )}
    </div>
  )
}