import { useState } from 'react'
import axios from 'axios'
import { UserPlus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { DISCOUNT_TYPE_LABEL } from '@/types'

// 手机号需要带国家代码（后端用 libphonenumber 解析）；中国手机号裸号码自动补 +86
function normalizePhone(phone: string): string {
  const p = phone.trim().replace(/[\s()-]/g, '')
  if (!p.startsWith('+') && /^1\d{10}$/.test(p)) return `+86${p}`
  return p
}

export function AddPassengerDialog({ onAdded }: { onAdded: () => void }) {
  const [open, setOpen] = useState(false)
  const [name, setName] = useState('')
  const [idNo, setIdNo] = useState('')
  const [phone, setPhone] = useState('')
  const [discountType, setDiscountType] = useState('1') // 默认成人
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    if (!discountType) {
      setError('请选择优惠(待)类型')
      return
    }
    if (!phone.trim()) {
      setError('请填写手机号')
      return
    }

    setSubmitting(true)
    try {
      await axios.post('/api/passengers', {
        name,
        idType: 1, // 目前仅支持中国居民身份证
        idNo,
        phone: normalizePhone(phone),
        discountType: Number(discountType),
      })
      setOpen(false)
      setName('')
      setIdNo('')
      setPhone('')
      setDiscountType('1')
      onAdded()
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        setError(err.response.data.message || '添加乘车人失败')
      } else {
        setError('网络错误，请稍后重试')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <Button type="button" onClick={() => { setError(''); setOpen(true) }}>
        <UserPlus data-icon="inline-start" />
        添加乘车人
      </Button>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>添加乘车人</DialogTitle>
            <DialogDescription>填写乘车人信息</DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="passenger-idtype">证件类型</FieldLabel>
                <Input id="passenger-idtype" value="中国居民身份证" disabled />
              </Field>
              <Field>
                <FieldLabel htmlFor="passenger-name">姓名</FieldLabel>
                <Input
                  id="passenger-name"
                  type="text"
                  value={name}
                  onChange={e => setName(e.target.value)}
                  placeholder="请输入真实姓名，以便购票"
                  required
                />
              </Field>
              <Field>
                <FieldLabel htmlFor="passenger-idno">证件号码</FieldLabel>
                <Input
                  id="passenger-idno"
                  type="text"
                  value={idNo}
                  onChange={e => setIdNo(e.target.value)}
                  placeholder="用于身份核验，请正确填写"
                  required
                />
              </Field>
              <Field>
                <FieldLabel>优惠(待)类型</FieldLabel>
                <Select value={discountType} onValueChange={setDiscountType}>
                  <SelectTrigger aria-invalid={error === '请选择优惠(待)类型'} className="w-full">
                    <SelectValue placeholder="请选择优惠(待)类型" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      {Object.entries(DISCOUNT_TYPE_LABEL).map(([code, label]) => (
                        <SelectItem key={code} value={code}>{label}</SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>
              </Field>
              <Field>
                <FieldLabel htmlFor="passenger-phone">手机号</FieldLabel>
                <Input
                  id="passenger-phone"
                  type="tel"
                  value={phone}
                  onChange={e => setPhone(e.target.value)}
                  placeholder="请填写乘车人手机号"
                />
              </Field>
              {error && (
                <p className="text-sm text-destructive">{error}</p>
              )}
            </FieldGroup>
            <DialogFooter className="mt-6">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>
                取消
              </Button>
              <Button type="submit" disabled={submitting}>
                {submitting ? '提交中...' : '提交'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  )
}