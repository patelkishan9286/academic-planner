import axios from '../../axiosConfig';

export const fetchEvents = async (startDate, endDate) => {
  const res = await axios.get(
    `/event?firstDate=${startDate}&secondDate=${endDate}`
  );
  return res;
};

export const addVariableEvent = async (data) => {
  return await axios.post('/event/variable', data);
};

export const updateFixedEvent = async (data) => {
  return await axios.put('/event/fixed', data);
};

export const updateVeriableEvent = async (data) => {
  return await axios.put('/event/variable', data);
};

export const deleteFixedEvent = async (eventId) => {
  return axios.delete('/event/fixed/' + eventId);
};
export const deleteVariableEvent = async (eventId) => {
  return axios.delete('/event/variable/' + eventId);
};

export const getFixedEvent = async (eventId) => {
  return axios.get('/event/fixed/' + eventId);
};
export const getVariableEvent = async (eventId) => {
  return axios.get('/event/variable/' + eventId);
};

export const rescheduleSuggestions = async () => {
  return axios.post('/event/reschedule');
};
