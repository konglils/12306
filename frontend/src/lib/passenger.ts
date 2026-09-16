import { parsePhoneNumberWithError } from 'libphonenumber-js'

// 用 libphonenumber-js 校验手机号（默认地区 CN）。合法则返回 E164 规格号码（如 +8613800138000），
// 与后端 parsePhoneE164 的入参要求一致；不合法返回 null。
export function toE164OrNull(phone: string): string | null {
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
export function isValidChinaIdNo(idNo: string): boolean {
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

// 乘车人表单字段（姓名、证件号码、手机号码、优惠类型）的统一校验和归一化。
// 校验不通过时返回 error，可直接展示给用户；通过时返回 values，即提交给后端的字段
// （姓名、证件号去首尾空格，手机号转 E164，优惠类型转数字）。
export function checkPassengerForm(
  name: string,
  idNo: string,
  phone: string,
  discountType: string,
): { error: string } | { values: { name: string; idNo: string; phone: string; discountType: number } } {
  const trimmedName = name.trim()
  if (!trimmedName) {
    return { error: '请填写姓名' }
  }
  if (!discountType) {
    return { error: '请选择优惠(待)类型' }
  }
  const trimmedIdNo = idNo.trim()
  if (!isValidChinaIdNo(trimmedIdNo)) {
    return { error: '身份证号格式错误' }
  }
  if (!phone.trim()) {
    return { error: '请填写手机号码' }
  }
  const e164 = toE164OrNull(phone)
  if (!e164) {
    return { error: '手机号码格式错误' }
  }
  return {
    values: {
      name: trimmedName,
      idNo: trimmedIdNo,
      phone: e164,
      discountType: Number(discountType),
    },
  }
}
