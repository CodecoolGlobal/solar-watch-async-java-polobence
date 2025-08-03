import { Routes, Route, Link, Navigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import Registration from './pages/Registration';
import Login from './pages/Login';
import SolarWatch from './pages/SolarWatch';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('token'));

  useEffect(() => {
    const handleStorageChange = () => {
      setIsAuthenticated(!!localStorage.getItem('token'));
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsAuthenticated(false);
    return <Navigate to="/" />;
  };

  return (
    <div className="app">
      <nav>
        <ul>
          <li><Link to="/">Home</Link></li>
          {!isAuthenticated ? (
            <>
              <li><Link to="/login">Login</Link></li>
              <li><Link to="/registration">Register</Link></li>
            </>
          ) : (
            <>
              <li><Link to="/solar-watch">SolarWatch</Link></li>
              <li><button onClick={handleLogout} className="logout-button">Logout</button></li>
            </>
          )}
        </ul>
      </nav>
      
      <Routes>
        <Route path="/login" element={<Login onLogin={() => setIsAuthenticated(true)} />} />
        <Route path="/registration" element={<Registration />} />
        <Route 
          path="/solar-watch" 
          element={
            <ProtectedRoute>
              <SolarWatch />
            </ProtectedRoute>
          } 
        />
        <Route path="/" element={
          <div className="home">
            <h1>Welcome to SolarWatch</h1>
            <p>Get information about sunrise and sunset times around the world.</p>
            <div className="cta-buttons">
              {!isAuthenticated ? (
                <>
                  <Link to="/login" className="cta-button">Login</Link>
                  <Link to="/registration" className="cta-button secondary">Register</Link>
                </>
              ) : (
                <Link to="/solar-watch" className="cta-button">Go to SolarWatch</Link>
              )}
            </div>
          </div>
        } />
      </Routes>
    </div>
  )
}

export default App;
