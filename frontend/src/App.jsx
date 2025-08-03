import { Routes, Route, Link } from 'react-router-dom';
import Registration from './components/Registration';
import './App.css'

function App() {

  return (
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
          </div>
        } />
      </Routes>
    </div>
  )
}

export default App
