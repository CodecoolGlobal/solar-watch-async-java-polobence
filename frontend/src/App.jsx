import { Routes, Route, Link } from 'react-router-dom';
import Registration from './pages/Registration';
import Login from './pages/Login';
import './App.css'

function App() {
  return (
    <div className="app">
      <nav>
        <ul>
          <li><Link to="/">Home</Link></li>
          <li><Link to="/login">Login</Link></li>
          <li><Link to="/registration">Register</Link></li>
        </ul>
      </nav>
      
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/registration" element={<Registration />} />
        <Route path="/" element={
          <div className="home">
            <h1>Welcome to SolarWatch</h1>
            <p>Get information about sunrise and sunset times around the world.</p>
            <div className="cta-buttons">
              <Link to="/login" className="cta-button">Login</Link>
              <Link to="/registration" className="cta-button secondary">Register</Link>
            </div>
          </div>
        } />
      </Routes>
    </div>
  )
}

export default App
