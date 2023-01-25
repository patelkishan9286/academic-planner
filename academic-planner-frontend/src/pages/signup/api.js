import axios from '../../axiosConfig';

export const sigupnRequest = async (data) => {
  return await axios.post('/auth/signup', data);
};
