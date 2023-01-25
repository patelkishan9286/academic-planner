import axios from '../../axiosConfig';

export const loginRequest = async (data) => {
  return await axios.post('/auth/login', data);
};
