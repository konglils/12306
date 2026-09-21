import { useState } from 'react'
import { Loader2, Pencil } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { PassengerDialog } from '@/components/PassengerDialog'
import type { Passenger } from '@/types'

// 选择乘车人对话框：左侧复选框选择，右侧铅笔按钮打开编辑对话框。
// 勾选状态由父组件持有（selectedIds），关闭再打开仍保持；
// 编辑对话框是叠加在上层的独立 Dialog，关闭后本对话框仍在。
export function PassengerPickerDialog({
  open,
  onOpenChange,
  passengers,
  loading,
  error,
  selectedIds,
  onToggle,
  onReload,
  onConfirm,
}: {
  open: boolean
  onOpenChange: (open: boolean) => void
  passengers: Passenger[]
  loading: boolean
  error: string
  /** 已勾选乘车人的 key 集合（`${idType}-${idNo}`） */
  selectedIds: ReadonlySet<string>
  onToggle: (key: string, checked: boolean) => void
  /** 编辑成功后刷新乘车人列表 */
  onReload: () => void
  onConfirm: () => void
}) {
  const [editTarget, setEditTarget] = useState<Passenger | null>(null)

  // 当前用户排在最上面，其余按姓名排序
  const sorted = [...passengers].sort((a, b) => {
    if (a.isUser !== b.isUser) return a.isUser ? -1 : 1
    return a.name.localeCompare(b.name, 'zh-Hans-CN')
  })

  function renderRow(p: Passenger) {
    const key = `${p.idType}-${p.idNo}`
    // 已通过(status===2)绿色，未通过(status===3)红色，待核验(status===1)灰色
    const nameColor = p.status === 2 ? 'text-success' : p.status === 3 ? 'text-destructive' : 'text-muted-foreground'
    return (
      <div key={key} className="flex items-center gap-3 py-2">
        <Checkbox
          checked={selectedIds.has(key)}
          onCheckedChange={checked => onToggle(key, checked === true)}
          aria-label={`选择乘车人 ${p.name}`}
        />
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-2">
            <span className={`text-base font-semibold ${nameColor}`}>{p.name}</span>
            {p.status === 1 && (
              <span className="text-sm text-muted-foreground">身份核验中</span>
            )}
            {p.status === 3 && (
              <span className="text-sm text-destructive">身份未通过</span>
            )}
          </div>
          <p className="mt-1 text-sm text-muted-foreground">{p.idNo}</p>
        </div>
        <Button
          type="button"
          variant="ghost"
          size="icon-sm"
          onClick={() => setEditTarget(p)}
          aria-label={`编辑乘车人 ${p.name}`}
        >
          <Pencil />
        </Button>
      </div>
    )
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>选择乘车人</DialogTitle>
          <DialogDescription>勾选本次乘车的乘车人</DialogDescription>
        </DialogHeader>

        <div className="max-h-80 overflow-y-auto">
          {loading && (
            <div className="flex justify-center py-10">
              <Loader2 className="size-8 animate-spin text-muted-foreground" />
            </div>
          )}
          {!loading && error && (
            <p className="py-10 text-center text-muted-foreground">{error}</p>
          )}
          {!loading && !error && passengers.length === 0 && (
            <p className="py-10 text-center text-muted-foreground">暂无乘车人</p>
          )}
          {!loading && !error && sorted.map(renderRow)}
        </div>

        <DialogFooter>
          <Button
            type="button"
            disabled={selectedIds.size === 0}
            onClick={() => { onConfirm(); onOpenChange(false) }}
          >
            确认
          </Button>
        </DialogFooter>

        {/* 叠加的编辑对话框：挂在本对话框内部，关闭它不影响本对话框 */}
        <PassengerDialog
          mode="edit"
          open={editTarget !== null}
          onOpenChange={o => { if (!o) setEditTarget(null) }}
          passenger={editTarget}
          onSuccess={onReload}
        />
      </DialogContent>
    </Dialog>
  )
}
