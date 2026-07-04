import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { Button } from '@/components/ui/button'
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

export function SignupForm({ ...props }: React.ComponentProps<typeof Card>) {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')

    if (password !== confirm) {
      setError('两次输入的密码不一致')
      return
    }

    setSubmitting(true)
    try {
      await axios.post('/api/users', { username, password })
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
          创建您的账户
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
