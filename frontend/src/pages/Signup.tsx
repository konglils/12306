import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

export default function Signup() {
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
    <div className="max-w-sm mx-auto">
      <section className="bg-card border border-stroke px-8 py-8">
        <h1 className="text-xl font-bold text-ink mb-6 text-center">注册</h1>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <Label>用户名</Label>
            <Input
              type="text"
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="6-30 位字母、数字或下划线"
            />
          </div>

          <div>
            <Label>密码</Label>
            <Input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="6-30 位字母、数字或下划线"
            />
          </div>

          <div>
            <Label>确认密码</Label>
            <Input
              type="password"
              value={confirm}
              onChange={e => setConfirm(e.target.value)}
              placeholder="请再次输入密码"
            />
          </div>

          {error && (
            <p className="text-sm text-accent">{error}</p>
          )}

          <Button type="submit" disabled={submitting}>
            {submitting ? '注册中...' : '注册'}
          </Button>
        </form>

        <p className="text-sm text-muted mt-4 text-center">
          已有账号？<Link to="/signin" className="text-primary hover:text-primary-hi">去登录</Link>
        </p>
      </section>
    </div>
  )
}
