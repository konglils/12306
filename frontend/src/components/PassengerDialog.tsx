import { useEffect, useState, type SubmitEvent } from 'react'
import axios from 'axios'
import { Button } from '@/components/ui/button'
import { PassengerFields } from '@/components/PassengerFields'
import { checkPassengerForm } from '@/lib/passenger'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { FieldGroup } from '@/components/ui/field'
import type { Passenger } from '@/types'

// mode 为 'add' 时打开的是添加对话框，'edit' 时是编辑对话框。
// 编辑模式下证件类型、姓名、证件号码取自 passenger 且不可修改，其余字段（优惠类型、手机号）可编辑。
export function PassengerDialog({
  mode,
  open,
  onOpenChange,
  passenger,
  onSuccess,
}: {
  mode: 'add' | 'edit'
  open: boolean
  onOpenChange: (open: boolean) => void
  /** 编辑模式下当前待编辑的乘车人，用于回填表单；添加模式下忽略 */
  passenger?: Passenger | null
  /** 添加或编辑成功后回调 */
  onSuccess?: () => void
}) {
  const [name, setName] = useState('')
  const [idNo, setIdNo] = useState('')
  const [phone, setPhone] = useState('')
  const [discountType, setDiscountType] = useState('1') // 默认成人
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState('')
  const [confirming, setConfirming] = useState(false)

  const isEdit = mode === 'edit'
  // 当前登录用户本人的乘车人不可删除，编辑对话框里不展示删除按钮
  const canDelete = isEdit && !!passenger && !passenger.isUser

  // 打开对话框（或切换编辑对象）时回填表单：编辑模式用乘车人数据，添加模式为空表单
  useEffect(() => {
    if (!open) return
    setName(passenger?.name.trim() ?? '')
    setIdNo(passenger?.idNo ?? '')
    setPhone(passenger?.phone ?? '')
    setDiscountType(String(passenger?.discountType ?? 1))
    setError('')
    setDeleteError('')
    setConfirming(false)
  }, [open, passenger])

  async function handleSubmit(e: SubmitEvent<HTMLFormElement>) {
    e.preventDefault()
    setError('')

    const result = checkPassengerForm(name, idNo, phone, discountType)
    if ('error' in result) {
      setError(result.error)
      return
    }

    setSubmitting(true)
    try {
      if (isEdit && passenger) {
        await axios.patch('/api/passengers', {
          ...result.values,
          isUser: passenger.isUser,
          idType: passenger.idType,
        })
      } else {
        await axios.post('/api/passengers', {
          ...result.values,
          isUser: false,
          idType: 1, // 目前仅支持中国居民身份证
        })
      }
      onOpenChange(false)
      onSuccess?.()
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        setError(err.response.data.message || (isEdit ? '修改乘车人失败' : '添加乘车人失败'))
      } else {
        setError('网络错误，请稍后重试')
      }
    } finally {
      setSubmitting(false)
    }
  }

  // 确认界面里点击"删除"后真正执行删除；成功则关闭对话框并刷新列表
  async function doDelete() {
    if (!passenger) return
    setDeleteError('')
    setDeleting(true)
    try {
      await axios.delete('/api/passengers', {
        params: { idType: passenger.idType, idNo: passenger.idNo },
      })
      onOpenChange(false)
      onSuccess?.()
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

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        {confirming ? (
          <>
            <DialogHeader>
              <DialogTitle>删除乘车人</DialogTitle>
              <DialogDescription>
                确定要删除乘车人“{passenger?.name.trim()}”吗？删除后需重新添加。
              </DialogDescription>
            </DialogHeader>
            {deleteError && (
              <p className="text-sm text-destructive">{deleteError}</p>
            )}
            <DialogFooter className="mt-6">
              <Button type="button" variant="outline" onClick={() => setConfirming(false)} disabled={deleting}>
                取消
              </Button>
              <Button type="button" variant="destructive" onClick={doDelete} disabled={deleting}>
                {deleting ? '删除中...' : '删除'}
              </Button>
            </DialogFooter>
          </>
        ) : (
          <form onSubmit={handleSubmit}>
          <FieldGroup>
            <PassengerFields
              idPrefix="passenger"
              idType={isEdit ? (passenger?.idType ?? 1) : 1}
              name={name}
              onNameChange={setName}
              idNo={idNo}
              onIdNoChange={setIdNo}
              phone={phone}
              onPhoneChange={setPhone}
              discountType={discountType}
              onDiscountTypeChange={setDiscountType}
              nameDisabled={isEdit}
              idNoDisabled={isEdit}
            />
            {error && (
              <p className="text-sm text-destructive">{error}</p>
            )}
          </FieldGroup>
          <DialogFooter className="mt-6">
            {canDelete && (
              <Button
                type="button"
                variant="outline"
                className="mr-auto text-destructive"
                onClick={() => setConfirming(true)}
                disabled={submitting}
              >
                删除
              </Button>
            )}
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              取消
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? '提交中...' : '提交'}
            </Button>
          </DialogFooter>
        </form>
        )}
      </DialogContent>
    </Dialog>
  )
}
