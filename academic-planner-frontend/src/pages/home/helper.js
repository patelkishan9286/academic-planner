import moment from 'moment';
import { WeekDays } from '../profileSetup/contsants';

export const formatEventData = (data) => {
  return data.map((event) => {
    return {
      start: moment(
        event.startDate + ' ' + event.startTime,
        'YYYY-MM-DD HH:mm:ss'
      ).toDate(),
      end: moment(
        event.endDate + ' ' + event.endTime,
        'YYYY-MM-DD HH:mm:ss'
      ).toDate(),
      title: event.name,
      eventType: event.eventType,
      ...event,
    };
  });
};

export const getFixedFormValue = (data) => {
  let { startDate, endDate, startTime, endTime, isRepeat, isReschedulable } =
    data;
  if (isRepeat) {
    endDate = data.repeatEvent.endDate;
    return {
      ...data,
      ...data.repeatEvent,
      endDate: moment(endDate),
      startDate: moment(startDate),
      startTime: moment(startTime, 'HH:mm:ss'),
      endTime: moment(endTime, 'HH:mm:ss'),
      eventType: !isReschedulable ? 'NO-EDIT' : 'FIXED',
      isReschedulable: isReschedulable.toString(),
      isRepeat: isRepeat.toString(),
      weekDays: data.repeatEvent.weeklyRepeatDays?.reduce((acc, val, index) => {
        if (val) {
          acc.push(WeekDays[index]);
        }
        return acc;
      }, []),
    };
  } else {
    return {
      ...data,
      startDate: moment(startDate),
      startTime: moment(startTime, 'HH:mm:ss'),
      endTime: moment(endTime, 'HH:mm:ss'),
      eventType: !isReschedulable ? 'NO-EDIT' : 'FIXED',
      isReschedulable: isReschedulable.toString(),
      isRepeat: isRepeat.toString(),
    };
  }
};

export const getSaveFixData = (data, values) => {
  let weeklyRepeatDays = [false, false, false, false, false, false, false];

  if (values.weekDays)
    values.weekDays.map((weekDay) => (weeklyRepeatDays[weekDay] = true));
  else if (data?.repeatEvent?.weeklyRepeatDays)
    weeklyRepeatDays = data.repeatEvent.weeklyRepeatDays;

  return {
    ...values,
    startDate: values.startDate.format('YYYY-MM-DD'),
    startTime: `${values.startTime.format('HH:mm')}`,
    endTime: `${values.endTime.format('HH:mm')}`,
    endDate: values.startDate.format('YYYY-MM-DD'),
    repeatEvent:
      data.isRepeat === 'true'
        ? {
            repititionType: values.repititionType,
            weeklyRepeatDays,
            endDate: values.endDate.format('YYYY-MM-DD'),
          }
        : null,
  };
};
