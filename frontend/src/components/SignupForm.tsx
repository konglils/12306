import { useState, type SubmitEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { PassengerFields } from '@/components/PassengerFields'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { checkPassengerForm } from '@/lib/passenger'

// 用户名：6-30 位字母、数字或下划线。
function validateUsername(username: string): string | null {
  if (!/^[A-Za-z0-9_]{6,30}$/.test(username)) {
    return '用户名需为 6-30 位字母、数字或下划线'
  }
  return null
}

// 密码：6-30 位字母、数字或下划线。
function validatePassword(password: string): string | null {
  if (!/^[A-Za-z0-9_]{6,30}$/.test(password)) {
    return '密码需为 6-30 位字母、数字或下划线'
  }
  return null
}

export function SignupForm({ ...props }: React.ComponentProps<typeof Card>) {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [name, setName] = useState('')
  const [idNo, setIdNo] = useState('')
  const [phone, setPhone] = useState('')
  const [discountType, setDiscountType] = useState('1') // 默认成人
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: SubmitEvent<HTMLFormElement>) {
    e.preventDefault()
    setError('')

    const usernameError = validateUsername(username)
    if (usernameError) {
      setError(usernameError)
      return
    }
    const passwordError = validatePassword(password)
    if (passwordError) {
      setError(passwordError)
      return
    }

    if (password !== confirm) {
      setError('两次输入的密码不一致')
      return
    }

    // 注册时同步创建本人乘车人，校验规则与添加乘车人一致
    const result = checkPassengerForm(name, idNo, phone, discountType)
    if ('error' in result) {
      setError(result.error)
      return
    }

    setSubmitting(true)
    try {
      await axios.post('/api/users', {
        username,
        password,
        passenger: {
          isUser: true,
          idType: 1, // 目前仅支持中国居民身份证
          ...result.values,
        },
      })
      toast.success('注册成功')
      navigate('/signin')
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        setError(err.response.data.message || '注册失败')
      } else {
        setError('网络错误，请稍后重试')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card {...props}>
      <CardHeader>
        <CardTitle>注册</CardTitle>
        <CardDescription>
          创建您的账户并填写本人乘车人信息
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit}>
          <FieldGroup>
            <Field>
              <FieldLabel htmlFor="username">用户名</FieldLabel>
              <Input
                id="username"
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="6-30 位字母、数字或下划线"
                autoFocus
                required
              />
            </Field>
            <Field>
              <FieldLabel htmlFor="password">密码</FieldLabel>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="6-30 位字母、数字或下划线"
                required
              />
            </Field>
            <Field>
              <FieldLabel htmlFor="confirm-password">确认密码</FieldLabel>
              <Input
                id="confirm-password"
                type="password"
                value={confirm}
                onChange={e => setConfirm(e.target.value)}
                placeholder="请再次输入密码"
                required
              />
            </Field>
            <PassengerFields
              idPrefix="signup"
              name={name}
              onNameChange={setName}
              idNo={idNo}
              onIdNoChange={setIdNo}
              phone={phone}
              onPhoneChange={setPhone}
              discountType={discountType}
              onDiscountTypeChange={setDiscountType}
            />
            {error && (
              <p className="text-sm text-destructive">{error}</p>
            )}
            <Field>
              <Button type="submit" disabled={submitting} className="w-full">
                {submitting ? '注册中...' : '注册'}
              </Button>
              <FieldDescription className="px-6 text-center">
                已有账号？<Link to="/signin">去登录</Link>
              </FieldDescription>
            </Field>
          </FieldGroup>
        </form>
      </CardContent>
    </Card>
  )
}
