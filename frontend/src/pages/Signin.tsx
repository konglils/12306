import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useAuth } from '../store/auth'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

export default function Signin() {
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
    <div className="max-w-sm mx-auto">
      <Card>
        <CardHeader>
          <CardTitle>登录</CardTitle>
        </CardHeader>
        <CardContent>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <Label>用户名</Label>
            <Input
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
            />
          </div>

          <div>
            <Label>密码</Label>
            <Input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
            />
          </div>

          {error && (
            <p className="text-sm text-destructive">{error}</p>
          )}

          <Button type="submit" disabled={submitting}>
            {submitting ? '登录中...' : '登录'}
          </Button>
        </form>

        <p className="text-sm text-muted-foreground mt-4 text-center">
          没有账号？<Link to="/signup" className="text-primary hover:text-primary/80">去注册</Link>
        </p>
        </CardContent>
      </Card>
    </div>
  )
}
