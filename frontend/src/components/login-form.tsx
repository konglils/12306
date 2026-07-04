import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { cn } from '@/lib/utils'
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
import { useAuth } from '../store/auth'

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<'div'>) {
  const navigate = useNavigate()
  const signin = useAuth(s => s.signin)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await signin(username, password)
      navigate('/tickets')
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        setError(err.response.data.message || '登录失败')
      } else {
        setError('网络错误，请稍后重试')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={cn('flex flex-col gap-6', className)} {...props}>
      <Card>
        <CardHeader>
          <CardTitle>登录</CardTitle>
          <CardDescription>
            输入您的用户名和密码登录
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
                  required
                />
              </Field>
              {error && (
                <p className="text-sm text-destructive">{error}</p>
              )}
              <Field>
                <Button type="submit" disabled={submitting} className="w-full">
                  {submitting ? '登录中...' : '登录'}
                </Button>
                <FieldDescription className="text-center">
                  没有账号？<Link to="/signup">去注册</Link>
                </FieldDescription>
              </Field>
            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
