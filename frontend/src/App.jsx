import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import Registration from './components/Registration';
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <Router>
      <div className="app">
        <nav>
          <ul>
            <li><Link to="/">Home</Link></li>
            <li><Link to="/registration">Register</Link></li>
          </ul>
        </nav>
        
        <Routes>
          <Route path="/registration" element={<Registration />} />
          <Route path="/" element={
            <div className="home">
              <h1>Welcome to SolarWatch</h1>
              <p>Get information about sunrise and sunset times around the world.</p>
              <div className="cta-buttons">
                <Link to="/registration" className="cta-button">Get Started</Link>
              </div>
              <div>
                <a href="https://vite.dev" target="_blank">
                  <img src={viteLogo} className="logo" alt="Vite logo" />
                </a>
                <a href="https://react.dev" target="_blank">
                  <img src={reactLogo} className="logo react" alt="React logo" />
                </a>
              </div>
              <h1>Vite + React</h1>
              <div className="card">
                <button onClick={() => setCount((count) => count + 1)}>
                  count is {count}
                </button>
                <p>
                  Edit <code>src/App.jsx</code> and save to test HMR
                </p>
              </div>
              <p className="read-the-docs">
                Click on the Vite and React logos to learn more
              </p>
            </div>
          } />
        </Routes>
      </div>
    </Router>
  )
}

export default App
