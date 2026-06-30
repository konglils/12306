import { useNavigate, useSearchParams } from 'react-router-dom'
import TicketSearch from '../components/TicketSearch'

export default function Home() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const from = searchParams.get('from') || ''
  const to = searchParams.get('to') || ''
  const date = searchParams.get('date') || ''

  function handleSearch(f: string, t: string, d: string) {
    navigate(`/tickets?from=${f}&to=${t}&date=${d}`)
  }

  return (
    <div>
      <TicketSearch
        fromCode={from}
        toCode={to}
        date={date}
        onSearch={handleSearch}
      />
    </div>
  )
}
