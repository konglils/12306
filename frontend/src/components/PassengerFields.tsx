import { Field, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { DISCOUNT_TYPE_LABEL, ID_TYPE_LABEL } from '@/types'

// 乘车人表单的公共字段：证件类型（固定展示，不可编辑）、姓名、证件号码、优惠(待)类型、手机号码。
// 注册表单和添加/编辑乘车人对话框共用；编辑模式下姓名和证件号码不可修改。
export function PassengerFields({
  idPrefix,
  idType = 1,
  name,
  onNameChange,
  idNo,
  onIdNoChange,
  phone,
  onPhoneChange,
  discountType,
  onDiscountTypeChange,
  nameDisabled = false,
  idNoDisabled = false,
}: {
  /** 字段 id 前缀，避免不同表单间的 id 冲突 */
  idPrefix: string
  /** 证件类型 code，目前仅支持中国居民身份证（1） */
  idType?: number
  name: string
  onNameChange: (value: string) => void
  idNo: string
  onIdNoChange: (value: string) => void
  phone: string
  onPhoneChange: (value: string) => void
  discountType: string
  onDiscountTypeChange: (value: string) => void
  /** 编辑模式下姓名不可修改 */
  nameDisabled?: boolean
  /** 编辑模式下证件号码不可修改 */
  idNoDisabled?: boolean
}) {
  return (
    <>
      <Field>
        <FieldLabel htmlFor={`${idPrefix}-idtype`}>证件类型</FieldLabel>
        <Input
          id={`${idPrefix}-idtype`}
          value={ID_TYPE_LABEL[idType]}
          disabled
        />
      </Field>
      <Field>
        <FieldLabel htmlFor={`${idPrefix}-name`}>姓名</FieldLabel>
        <Input
          id={`${idPrefix}-name`}
          type="text"
          value={name}
          onChange={e => onNameChange(e.target.value)}
          placeholder="请输入真实姓名，以便购票"
          disabled={nameDisabled}
          required
        />
      </Field>
      <Field>
        <FieldLabel htmlFor={`${idPrefix}-idno`}>证件号码</FieldLabel>
        <Input
          id={`${idPrefix}-idno`}
          type="text"
          value={idNo}
          onChange={e => onIdNoChange(e.target.value)}
          placeholder="用于身份核验，请正确填写"
          disabled={idNoDisabled}
          required
        />
      </Field>
      <Field>
        <FieldLabel>优惠(待)类型</FieldLabel>
        <Select value={discountType} onValueChange={onDiscountTypeChange}>
          <SelectTrigger className="w-full">
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
        <FieldLabel htmlFor={`${idPrefix}-phone`}>手机号码</FieldLabel>
        <Input
          id={`${idPrefix}-phone`}
          type="tel"
          value={phone}
          onChange={e => onPhoneChange(e.target.value)}
          placeholder="请填写手机号码"
          required
        />
      </Field>
    </>
  )
}
