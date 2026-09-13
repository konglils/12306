import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import { Trash2, UserPlus } from 'lucide-react'
import { pinyin } from 'pinyin-pro'
import type { Passenger } from '@/types'
import { DISCOUNT_TYPE_LABEL } from '@/types'
import { useAuth } from '@/store/auth'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { PassengerDialog } from '@/components/PassengerDialog'

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
  const [addOpen, setAddOpen] = useState(false)
  const [editTarget, setEditTarget] = useState<Passenger | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Passenger | null>(null)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState('')

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

  async function confirmDelete() {
    if (!deleteTarget) return
    setDeleting(true)
    setDeleteError('')
    try {
      await axios.delete('/api/passengers', {
        params: { idType: deleteTarget.idType, idNo: deleteTarget.idNo },
      })
      setDeleteTarget(null)
      loadPassengers()
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        setDeleteError(err.response.data.message || '删除乘车人失败')
      } else {
        setDeleteError('网络错误，请稍后重试')
      }
    } finally {
      setDeleting(false)
    }
  }

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
    return (
      <Card
        key={key}
        onClick={() => setEditTarget(p)}
        className="cursor-pointer"
      >
        <CardContent>
          <div className="flex items-center gap-2">
            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-2">
                <span className="text-base font-semibold">{p.name}</span>
                <span className="rounded bg-muted px-1.5 py-0.5 text-xs text-muted-foreground">
                  {DISCOUNT_TYPE_LABEL[p.discountType] ?? '其他'}
                </span>
              </div>
              <p className="mt-1 text-sm text-muted-foreground">{p.idNo}</p>
            </div>
            {!p.isUser && (
              <Button
                type="button"
                variant="ghost"
                size="icon-sm"
                onClick={(e) => { e.stopPropagation(); setDeleteError(''); setDeleteTarget(p) }}
                aria-label={`删除乘车人 ${p.name}`}
              >
                <Trash2 />
              </Button>
            )}
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-xl font-bold">乘车人</h1>
        <Button type="button" onClick={() => setAddOpen(true)}>
          <UserPlus data-icon="inline-start" />
          添加乘车人
        </Button>
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

      <PassengerDialog
        mode="add"
        open={addOpen}
        onOpenChange={setAddOpen}
        onSuccess={loadPassengers}
      />
      <PassengerDialog
        mode="edit"
        open={editTarget !== null}
        onOpenChange={open => { if (!open) setEditTarget(null) }}
        passenger={editTarget}
        onSuccess={loadPassengers}
      />

      <Dialog open={deleteTarget !== null} onOpenChange={open => { if (!open) setDeleteTarget(null) }}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>删除乘车人</DialogTitle>
            <DialogDescription>
              确定要删除乘车人“{deleteTarget?.name}”吗？删除后需重新添加。
            </DialogDescription>
          </DialogHeader>
          {deleteError && (
            <p className="text-sm text-destructive">{deleteError}</p>
          )}
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => setDeleteTarget(null)} disabled={deleting}>
              取消
            </Button>
            <Button type="button" variant="destructive" onClick={confirmDelete} disabled={deleting}>
              {deleting ? '删除中...' : '删除'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}