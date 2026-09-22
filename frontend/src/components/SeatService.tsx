import { Fragment } from 'react'
import { Minus, Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import type { BerthLevel } from '@/lib/seatService'

// 座位列固定顺序，与选座状态数组的 5 个列下标一一对应，过道固定在 C 与 D 之间。
// A 左侧靠窗、B 左侧中间、C 左侧靠走廊、D 右侧靠走廊、F 右侧靠窗。
const SEAT_COLS = ['A', 'B', 'C', 'D', 'F'] as const

// 选座服务：固定两排座位，点击切换选中；
// 已选座位数达到乘车人数量后再点击无效果，取消选择总是允许
export function SeatSelector({
  letters,
  count,
  value,
  onChange,
}: {
  /** 该席别可用的座位字母（如 ['A','B','C','D','F']） */
  letters: string[]
  /** 乘车人数量 */
  count: number
  /** 选座状态，固定为 2 行 × 5 列的数组 */
  value: boolean[][]
  onChange: (value: boolean[][]) => void
}) {
  const selectedTotal = value.flat().filter(Boolean).length

  function toggle(row: number, col: number) {
    if (value[row][col]) {
      const next = value.map(r => [...r])
      next[row][col] = false
      onChange(next)
      return
    }
    if (selectedTotal >= count) return
    const next = value.map(r => [...r])
    next[row][col] = true
    onChange(next)
  }

  // 一位乘车人只需选一个座位，只显示一排；多位乘车人可选两排
  const rowCount = count === 1 ? 1 : 2

  return (
    <div className="flex flex-col gap-2">
      {value.slice(0, rowCount).map((row, r) => (
        <div key={r} className="flex items-center gap-2">
          {SEAT_COLS.map((letter, c) => (
            <Fragment key={letter}>
              {letter === 'A' && (
                <span className="px-1 text-xs text-muted-foreground">靠窗</span>
              )}
              {letters.includes(letter) && (
                <button
                  type="button"
                  onClick={() => toggle(r, c)}
                  className={`flex size-9 items-center justify-center rounded-md border text-sm font-medium transition-colors ${
                    row[c]
                      ? 'border-primary bg-primary text-primary-foreground'
                      : 'border-input text-foreground hover:bg-muted/50'
                  }`}
                  aria-label={`第${r + 1}排 ${letter} 座`}
                >
                  {letter}
                </button>
              )}
              {letter === 'C' && (
                <span className="px-1 text-xs text-muted-foreground">过道</span>
              )}
              {letter === 'F' && (
                <span className="px-1 text-xs text-muted-foreground">靠窗</span>
              )}
            </Fragment>
          ))}
        </div>
      ))}
    </div>
  )
}

const BERTH_LABEL: Record<BerthLevel, string> = {
  lower: '下铺',
  middle: '中铺',
  upper: '上铺',
}

// 选铺服务：分别增减各铺位票数；
// 上中下铺加起来不能超过乘车人数量，超出时按 + 无效果
export function BerthSelector({
  levels,
  count,
  value,
  onChange,
}: {
  /** 该席别可选的铺位层级（无中铺的席别不含 middle） */
  levels: BerthLevel[]
  /** 乘车人数量 */
  count: number
  /** 各铺位已选票数 */
  value: Record<BerthLevel, number>
  onChange: (value: Record<BerthLevel, number>) => void
}) {
  const total = levels.reduce((sum, l) => sum + value[l], 0)

  function adjust(level: BerthLevel, delta: number) {
    const next = { ...value, [level]: value[level] + delta }
    if (next[level] < 0) return
    if (levels.reduce((sum, l) => sum + next[l], 0) > count) return
    onChange(next)
  }

  return (
    <div className="flex flex-col gap-2">
      {levels.map(level => (
        <div key={level} className="flex items-center">
          <span className="flex-1 text-sm text-foreground">选择{BERTH_LABEL[level]}</span>
          <Button
            type="button"
            variant="outline"
            size="icon-sm"
            disabled={value[level] === 0}
            onClick={() => adjust(level, -1)}
            aria-label={`减少${BERTH_LABEL[level]}票数`}
          >
            <Minus />
          </Button>
          <span className="w-10 text-center text-sm tabular-nums">{value[level]}</span>
          <Button
            type="button"
            variant="outline"
            size="icon-sm"
            disabled={total >= count}
            onClick={() => adjust(level, 1)}
            aria-label={`增加${BERTH_LABEL[level]}票数`}
          >
            <Plus />
          </Button>
        </div>
      ))}
    </div>
  )
}
