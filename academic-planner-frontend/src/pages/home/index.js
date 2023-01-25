import React, { useEffect, useState } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import EventForm from './components/eventForm';
import dayjs from 'dayjs';
import {
  deleteFixedEvent,
  deleteVariableEvent,
  fetchEvents,
  getFixedEvent,
  getVariableEvent,
  rescheduleSuggestions,
} from './api';
import { formatEventData, getFixedFormValue } from './helper';
import { Spin, message } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import EventMenu from './components/eventMenu';

const localizer = momentLocalizer(moment);
const momentFormatString = 'ddd MMM DD YYYY HH:mm:ss [GMT]ZZ';
const formatString = 'HH:mm:ss';

const CalendarViewHome = () => {
  const DATE_FORMAT = 'YYYY-MM-DD';
  const navigate = useNavigate();
  useEffect(() => {
    if (!localStorage.getItem('jwtToken')) navigate('/login');
    else {
      var today = moment();
      const start = today.startOf('week').format(DATE_FORMAT);
      const end = today.endOf('week').format(DATE_FORMAT);

      fetchEventsByRange(start, end);
    }
    // eslint-disable-next-line
  }, []);

  const [events, setEvents] = useState([]);

  const handleEdit = async (event) => {
    setIsLoading(true);
    try {
      if (event.eventType === 'FIXED') {
        const { data } = await getFixedEvent(event.id);
        setFormValues({
          ...blanckForm,
          isAddEvent: false,
          isModalOpen: true,
          data: getFixedFormValue(data),
        });
      } else {
        const { data } = await getVariableEvent(event.id);
        const duration = moment.duration(data.duration);
        setFormValues({
          ...blanckForm,
          isAddEvent: false,
          isModalOpen: true,
          data: {
            ...data,
            eventDate: moment(
              moment(data.deadline).format('YYYY-MM-DD'),
              'YYYY-MM-DD'
            ),
            startTime: dayjs(
              moment(data.deadline).format(formatString),
              formatString
            ),
            days: Math.floor(duration.hours() / 24),
            hours:
              (duration.hours() % 24) + Math.floor(duration.minutes() / 60),
            minutes:
              (duration.minutes() % 60) + Math.floor(duration.seconds() / 60),
            eventType: 'VARIABLE',
          },
        });
      }
    } catch (e) {
      message.error('Error while updating event');
    }
    setIsLoading(false);
  };

  const handleDelete = async (event) => {
    setIsLoading(true);
    try {
      if (event.eventType === 'FIXED') {
        await deleteFixedEvent(event.id);
      } else {
        await deleteVariableEvent(event.id);
      }
      message.success('Event deleted successfully');
    } catch (e) {
      message.error('Error while deleting event');
    }
    setIsLoading(false);
  };

  const CustomEvent = (data) => {
    const { event } = data;

    return (
      <EventMenu
        event={event}
        onEdit={() => handleEdit(event)}
        onDelete={() => handleDelete(event)}
      >
        <div {...data}>
          <div className='rbc-event-content' title={event.title}>
            {event.title}
          </div>
        </div>
      </EventMenu>
    );
  };

  const blanckForm = {
    isAddEvent: true,
    isModalOpen: false,
    handleCancel: () =>
      setFormValues({ ...formValues, isModalOpen: false, data: {} }),
    data: {},
  };
  const [formValues, setFormValues] = useState({ ...blanckForm });

  const [startDate, setStartDate] = useState(moment());

  const [isLoading, setIsLoading] = useState(false);

  const fetchEventsByRange = async (start, end) => {
    setIsLoading(true);
    try {
      const res = await fetchEvents(start, end);
      setEvents(formatEventData(res.data));
    } catch (e) {
      message.error('Something went wrong while fetching data');
    }
    setIsLoading(false);
  };

  const handleNavigate = (dateOrObject, view) => {
    const { start, end } =
      dateOrObject instanceof Date
        ? {
            start: dateOrObject,
            end: moment(dateOrObject).endOf(view).toDate(),
          }
        : dateOrObject;
    setStartDate(moment(start));
    fetchEventsByRange(
      moment(start).startOf(view).format(DATE_FORMAT),
      moment(end).format(DATE_FORMAT)
    );
  };

  const handleSelect = ({ start, end }) => {
    const momDate = moment(start, momentFormatString);

    setFormValues({
      ...blanckForm,
      isAddEvent: true,
      isModalOpen: true,
      data: {
        eventDate: moment(momDate.format('L'), 'L'),
        startTime: dayjs(momDate.format(formatString), formatString),
        endTime: dayjs(
          moment(end, momentFormatString).format(formatString),
          formatString
        ),
      },
    });
  };

  const rescheduleSuggestionEvents = async () => {
    await rescheduleSuggestions();
    window.location.reload();
  };

  const CustomToolbar = (props) => (
    <div className='rbc-toolbar'>
      <span className='rbc-btn-group'>
        <span className='rbc-btn-group'>
          <button onClick={() => props.onNavigate('TODAY')}>Today</button>
          <button onClick={() => props.onNavigate('PREV')}>Back</button>
          <button onClick={() => props.onNavigate('NEXT')}>Next</button>
        </span>
        <span className='rbc-btn-group'>
          <button onClick={rescheduleSuggestionEvents}>Reschedule</button>
        </span>
      </span>
      <span className='rbc-toolbar-label'>{props.label}</span>
      <span className='rbc-btn-group'>
        {props.views.map((view) => (
          <button
            key={view}
            className={view === props.view ? 'rbc-active' : ''}
            onClick={() => props.onView(view)}
          >
            {view}
          </button>
        ))}
      </span>
      <span className='rbc-btn-group'>
        <button
          onClick={() => {
            localStorage.removeItem('jwtToken');
            navigate('/login');
          }}
        >
          Logout
        </button>
      </span>
    </div>
  );

  const handleViewChange = (view, temp) => {
    fetchEventsByRange(
      moment(startDate).startOf(view).format(DATE_FORMAT),
      moment(startDate).endOf(view).format(DATE_FORMAT)
    );
  };

  const eventStyleGetter = (event, start, end, isSelected) => {
    const backgroundColor = event.eventType === 'FIXED' ? undefined : '#6F2DA8';
    const style = {
      backgroundColor,
      borderRadius: '5px',
      opacity: 0.8,
      color: 'white',
      border: '0px',
      display: 'block',
    };
    return {
      style: style,
    };
  };

  const views = {
    month: true,
    week: true,
    day: true,
    agenda: false,
  };

  return (
    <div className='myCustomHeight'>
      <Spin
        spinning={isLoading}
        tip='Loading...'
        indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />}
      >
        <Calendar
          localizer={localizer}
          events={events}
          defaultDate={new Date()}
          defaultView={'week'}
          startAccessor='start'
          endAccessor='end'
          onView={handleViewChange}
          views={views}
          components={{
            toolbar: CustomToolbar,
            event: CustomEvent,
          }}
          selectable
          onNavigate={handleNavigate}
          onSelectSlot={handleSelect}
          style={{ height: 700, margin: '20px' }}
          eventPropGetter={eventStyleGetter}
        />
      </Spin>
      {formValues.isModalOpen && <EventForm {...formValues} />}
    </div>
  );
};

export default CalendarViewHome;
