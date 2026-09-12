import { useState } from 'react'
import axios from 'axios'
import { UserPlus } from 'lucide-react'
import { parsePhoneNumberWithError } from 'libphonenumber-js'
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

// 用 libphonenumber-js 校验手机号（默认地区 CN）。合法则返回 E164 规格号码（如 +8613800138000），
// 与后端 parsePhoneE164 的入参要求一致；不合法返回 null。
function toE164OrNull(phone: string): string | null {
  const trimmed = phone.trim()
  if (!trimmed) return null
  try {
    const number = parsePhoneNumberWithError(trimmed, 'CN')
    return number.isValid() ? number.number : null
  } catch {
    // 无法解析（如随机字符）抛 ParseError，一律视为格式错误
    return null
  }
}

// 18 位、前 17 位为数字、出生日期码是真实日期（含闰年校验）、按权重计算校验码（末位可为 X/x）。
function isValidChinaIdNo(idNo: string): boolean {
  if (idNo.length !== 18) return false
  for (let i = 0; i < 17; i += 1) {
    if (!/[0-9]/.test(idNo[i])) return false
  }

  // 地址码不做校验。断言出生日期码能解析为真实存在的日期。
  const year = Number(idNo.slice(6, 10))
  const month = Number(idNo.slice(10, 12))
  const day = Number(idNo.slice(12, 14))
  const date = new Date(Date.UTC(year, month - 1, day))
  const birthDateValid =
    date.getUTCFullYear() === year &&
    date.getUTCMonth() === month - 1 &&
    date.getUTCDate() === day
  if (!birthDateValid) return false

  // 计算校验码
  const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
  const verifyCodes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
  let sum = 0
  for (let i = 0; i < 17; i += 1) {
    sum += Number(idNo[i]) * weights[i]
  }
  const realCode = verifyCodes[sum % 11]
  const givenCode = idNo[17].toUpperCase()
  return realCode === givenCode
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
    const trimmedName = name.trim()
    if (!trimmedName) {
      setError('请填写姓名')
      return
    }
    if (!discountType) {
      setError('请选择优惠(待)类型')
      return
    }
    if (idNo && !isValidChinaIdNo(idNo)) {
      setError('身份证号格式错误')
      return
    }
    if (!phone.trim()) {
      setError('请填写手机号码')
      return
    }
    const e164 = toE164OrNull(phone)
    if (!e164) {
      setError('手机号码格式错误')
      return
    }

    setSubmitting(true)
    try {
      await axios.post('/api/passengers', {
        name: trimmedName,
        idType: 1, // 目前仅支持中国居民身份证
        idNo,
        phone: e164,
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
                <FieldLabel htmlFor="passenger-phone">手机号码</FieldLabel>
                <Input
                  id="passenger-phone"
                  type="tel"
                  value={phone}
                  onChange={e => setPhone(e.target.value)}
                  placeholder="请填写乘车人手机号码"
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