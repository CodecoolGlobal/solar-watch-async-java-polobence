import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './SolarWatch.css';

function SolarWatch() {
  const [city, setCity] = useState('');
  const [date, setDate] = useState('');
  const [sunTimes, setSunTimes] = useState(null);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const today = new Date().toISOString().split('T')[0];
    setDate(today);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!city.trim()) {
      setError('Please enter a city name');
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login', { state: { from: '/solar-watch' } });
        return;
      }

      const formattedDate = date || new Date().toISOString().split('T')[0];
      
      const response = await fetch(`/api/suntimes/search?city=${encodeURIComponent(city)}&date=${formattedDate}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        }
      });

      if (response.status === 401) {
        localStorage.removeItem('token');
        navigate('/login', { 
          state: { 
            from: '/solar-watch',
            error: 'Your session has expired. Please log in again.'
          } 
        });
        return;
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      const sunTimesData = {
        ...data.sunTimes,
        city: data.sunTimes.city.name,
        country: data.sunTimes.city.country
      };
      
      setSunTimes(sunTimesData);
    } catch (err) {
      setError(err.message || 'An error occurred while fetching sun times');
      setSunTimes(null);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="solar-watch-container">
      <h1>SolarWatch</h1>
      <p>Check sunrise and sunset times for any city</p>

      <form onSubmit={handleSubmit} className="solar-form">
        <div className="form-group">
          <label htmlFor="city">City</label>
          <input
            type="text"
            id="city"
            value={city}
            onChange={(e) => setCity(e.target.value)}
            placeholder="Enter city name"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="date">Date</label>
          <input
            type="date"
            id="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            required
          />
        </div>

        <button type="submit" className="search-button" disabled={isLoading}>
          {isLoading ? 'Searching...' : 'Get Sun Times'}
        </button>
      </form>

      {error && <div className="error-message">{error}</div>}

      {sunTimes && (
        <div className="sun-times">
          <h2>Sun Times for {sunTimes.city}, {sunTimes.country}</h2>
          <p>Date: {new Date(sunTimes.date).toLocaleDateString()}</p>
          <div className="time-cards">
            <div className="time-card sunrise">
              <h3>Sunrise</h3>
              <p>{sunTimes.sunrise || 'Not available'}</p>
            </div>
            <div className="time-card sunset">
              <h3>Sunset</h3>
              <p>{sunTimes.sunset || 'Not available'}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SolarWatch;
