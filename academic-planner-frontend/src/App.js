import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import CalendarViewHome from './pages/home';
import LoginPage from './pages/login';
import ProfileSetup from './pages/profileSetup';
import SignupPage from './pages/signup';
import { fullHeight } from './styles';

function App() {
  return (
    <Router style={fullHeight}>
      <Routes>
        <Route path='/' element={<LoginPage />} />
        <Route path='/login' element={<LoginPage />} />
        <Route path='/sign-up' element={<SignupPage />} />
        <Route path='/profile-setup' element={<ProfileSetup />} />
        <Route path='/home' element={<CalendarViewHome />} />
      </Routes>
    </Router>
  );
}

export default App;
