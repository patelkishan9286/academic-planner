import axios from '../../axiosConfig';

export const addEvent = async (data) => {
  return await axios.post('/event/fixed', data);
};

export const setUserProfileComplete = async () => {
  return await axios.put('/auth/user/meta/status');
};
