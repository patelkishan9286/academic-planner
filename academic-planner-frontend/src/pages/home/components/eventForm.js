import React, { useEffect, useState } from 'react';
import {
  TimePicker,
  Modal,
  Form,
  Input,
  Row,
  Col,
  Select,
  Radio,
  InputNumber,
  DatePicker,
  message,
} from 'antd';
import { WeekDays } from '../../profileSetup/contsants';
import moment from 'moment';
import {
  addVariableEvent,
  updateFixedEvent,
  updateVeriableEvent,
} from '../api';
import { addEvent } from '../../profileSetup/api';

const EventForm = (props) => {
  const [form] = Form.useForm();

  const [eventType, setEventType] = useState(props.data.eventType);
  const [fixEventFormMete, setFixEventFormMete] = useState({
    isRepeat: props.data.isRepeat === 'true',
    isDaily: props.data.repititionType === 'DAILY',
  });

  useEffect(() => {
    !props.isModalOpen && form.resetFields();
    // eslint-disable-next-line
  }, [props]);

  const onSubmit = () => {
    let weeklyRepeatDays = [false, false, false, false, false, false, false];

    form
      .validateFields()
      .then(async (values) => {
        if (props.data.eventType === 'FIXED') {
          if (values.weekDays)
            values.weekDays.map(
              (weekDay) => (weeklyRepeatDays[weekDay] = true)
            );
          values = {
            ...props.data,
            ...values,
            startDate: values.startDate.format('YYYY-MM-DD'),
            endDate: values.startDate.format('YYYY-MM-DD'),
            startTime: `${values.startTime.format('HH:mm')}`,
            endTime: `${values.endTime.format('HH:mm')}`,
            isRepeat: values.isRepeat === 'true',
            repeat: values.isRepeat === 'true',
            reschedulable: values.isReschedulable === 'true',
            isReschedulable: values.isReschedulable === 'true',
            repeatEvent:
              values.isRepeat === 'true'
                ? {
                    repititionType: values.repititionType,
                    weeklyRepeatDays: fixEventFormMete.isDaily
                      ? undefined
                      : weeklyRepeatDays,
                    endDate: values.endDate.format('YYYY-MM-DD'),
                  }
                : null,
          };
        } else if (props.data.eventType === 'VARIABLE') {
          values = {
            ...values,
            deadline: moment(
              moment(values.eventDate).format('YYYY-MM-DD') +
                ' ' +
                moment(values.startTime).format('HH:mm:ss')
            ).format('YYYY-MM-DDTHH:mm:ss'),
            duration: `PT${values.days * 24 + values.hours}H${values.minutes}M`,
          };
        } else {
          values = {
            ...props.data,
            name: values.name,
            details: values.details,
            startDate: props.data.startDate.format('YYYY-MM-DD'),
            endDate: props.data.startDate.format('YYYY-MM-DD'),
            startTime: `${props.data.startTime.format('HH:mm')}`,
            endTime: `${props.data.endTime.format('HH:mm')}`,
            isRepeat: props.data.isRepeat === 'true',
            isReschedulable: props.data.isReschedulable === 'true',
            eventType: 'FIXED',
          };
        }

        delete values.weekDays;
        delete values.weeklyRepeatDays;
        delete values.repititionType;
        if (props.data.id) {
          if (props.data.eventType === 'VARIABLE') {
            await updateVeriableEvent(values);
          } else {
            await updateFixedEvent(values);
          }
        } else {
          if (props.data.eventType === 'VARIABLE') {
            await addVariableEvent(values);
          } else {
            await addEvent(values);
          }
        }
        form.resetFields();
      })
      .catch((err) => {
        message.error(
          `Something went wrong whhile ${
            props.isAddEvent ? 'creating' : 'updating'
          } event`
        );
      });
  };
  const validateTime = (_, values) => {
    const { days, hours, minutes } = form.getFieldsValue([
      'days',
      'hours',
      'minutes',
    ]);

    if (!days && !hours && !minutes) {
      return Promise.reject('Please Enter Approx Time Duratione!');
    }
    return Promise.resolve();
  };
  const renderVariableFields = () => {
    return (
      <>
        <Form.Item
          name='eventDate'
          label='Date of Deadline'
          rules={[{ required: true, message: 'Please Select a Day!' }]}
        >
          <DatePicker format={'YYYY-MM-DD'} />
        </Form.Item>

        <Form.Item
          label='Time of Deadline'
          name={'startTime'}
          rules={[
            {
              type: 'object',
              required: 'true',
              message: 'Select Start Time!',
            },
          ]}
        >
          <TimePicker format={'HH:mm'} />
        </Form.Item>

        <Form.Item
          label='Priority'
          name='eventPriority'
          rules={[
            {
              required: 'true',
              message: 'Select Priority!',
            },
          ]}
        >
          <Radio.Group>
            <Radio.Button value='HIGH'>High</Radio.Button>
            <Radio.Button value='MEDIUM'>Medium</Radio.Button>
            <Radio.Button value='LOW'>Low</Radio.Button>
          </Radio.Group>
        </Form.Item>

        <Form.Item
          label='Approx Time Duration'
          name={'duration'}
          validateTrigger='onSubmit'
          rules={[{ validator: validateTime }]}
        >
          <Row gutter={8}>
            <Col span={8}>
              <Form.Item name='days' style={{ marginBottom: '0px' }}>
                <InputNumber
                  placeholder='Days'
                  min={0}
                  max={999}
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name='hours' style={{ marginBottom: '0px' }}>
                <InputNumber
                  placeholder='Hours'
                  min={0}
                  max={23}
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name='minutes' style={{ marginBottom: '0px' }}>
                <InputNumber
                  placeholder='Minutes'
                  min={0}
                  max={59}
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </Col>
          </Row>
        </Form.Item>
      </>
    );
  };

  const renderFixFields = () => (
    <>
      <Form.Item
        name='startDate'
        label='Event Date'
        rules={[{ required: true, message: 'Please select a date!' }]}
      >
        <DatePicker format={'YYYY-MM-DD'} />
      </Form.Item>
      <Row>
        <Col span={12}>
          <Form.Item
            label='Start Time'
            name={'startTime'}
            rules={[
              {
                type: 'object',
                required: 'true',
                message: 'Select Start Time!',
              },
            ]}
          >
            <TimePicker format={'HH:mm'} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            label='End Time'
            name={'endTime'}
            rules={[
              {
                type: 'object',
                required: 'true',
                message: 'Select End Time!',
              },
            ]}
          >
            <TimePicker format={'HH:mm'} />
          </Form.Item>
        </Col>
      </Row>
      <Form.Item
        label='Reschedulable'
        name='isReschedulable'
        rules={[
          {
            required: 'true',
            message: 'Select Reschedulable!',
          },
        ]}
      >
        <Radio.Group>
          <Radio.Button value='true'>Yes</Radio.Button>
          <Radio.Button value='false'>No</Radio.Button>
        </Radio.Group>
      </Form.Item>
      <Form.Item
        label='Priority'
        name='eventPriority'
        rules={[
          {
            required: 'true',
            message: 'Select Priority!',
          },
        ]}
      >
        <Radio.Group>
          <Radio.Button value='HIGH'>High</Radio.Button>
          <Radio.Button value='MEDIUM'>Medium</Radio.Button>
          <Radio.Button value='LOW'>Low</Radio.Button>
        </Radio.Group>
      </Form.Item>
      <Form.Item
        label='Repetable'
        name='isRepeat'
        rules={[
          {
            required: 'true',
            message: 'Select Repetable!',
          },
        ]}
      >
        <Radio.Group
          onChange={(event) =>
            setFixEventFormMete({
              ...fixEventFormMete,
              isRepeat: event.target.value === 'true',
            })
          }
        >
          <Radio.Button value='true'>Yes</Radio.Button>
          <Radio.Button value='false'>No</Radio.Button>
        </Radio.Group>
      </Form.Item>
      {fixEventFormMete.isRepeat && (
        <>
          <Form.Item
            name='endDate'
            label='End Date'
            rules={[{ required: true, message: 'Please select a date!' }]}
          >
            <DatePicker format={'YYYY-MM-DD'} />
          </Form.Item>
          <Form.Item
            name='repititionType'
            label='Repitition Type'
            hasFeedback
            rules={[{ required: true, message: 'Please select type!' }]}
          >
            <Select
              placeholder='Select repitition type'
              options={[
                { value: 'DAILY', label: 'Daily' },
                { value: 'WEEKLY', label: 'Weekly' },
              ]}
              defaultValue={'DAILY'}
              onChange={(value) =>
                setFixEventFormMete({
                  ...fixEventFormMete,
                  isDaily: value === 'DAILY',
                })
              }
            ></Select>
          </Form.Item>
          {!fixEventFormMete.isDaily && (
            <Form.Item
              name='weekDays'
              label='Day of Week'
              hasFeedback
              rules={[
                { required: true, message: 'Please select week day(s)!' },
              ]}
            >
              <Select
                mode='multiple'
                allowClear
                placeholder='Select week day(s)'
                options={WeekDays}
              ></Select>
            </Form.Item>
          )}
        </>
      )}
    </>
  );

  const renderEventFields = () => {
    switch (eventType) {
      case 'FIXED':
        return renderFixFields();
      case 'VARIABLE':
        return renderVariableFields();
      default:
        return <></>;
    }
  };

  return props.isModalOpen ? (
    <Modal
      title={props.isAddEvent ? 'Add New Schedule' : 'Edit Schedule'}
      open={props.isModalOpen}
      onOk={onSubmit}
      okText={'Save'}
      onCancel={props.handleCancel}
    >
      <Form
        form={form}
        initialValues={props.isAddEvent ? undefined : props.data}
      >
        <Form.Item
          name={'name'}
          label='Name'
          rules={[{ required: 'true', message: 'Enter Schedule Name!' }]}
        >
          <Input placeholder='Enter Name' />
        </Form.Item>

        <Form.Item label='Description' name={'details'}>
          <Input placeholder='Enter Description' />
        </Form.Item>

        {props.isAddEvent && (
          <Form.Item
            label='Evenet Type'
            name='eventType'
            rules={[
              {
                required: 'true',
                message: 'Select Event Type!',
              },
            ]}
          >
            <Radio.Group onChange={(event) => setEventType(event.target.value)}>
              <Radio.Button value='FIXED'>Fixed</Radio.Button>
              <Radio.Button value='VARIABLE'>Variable</Radio.Button>
            </Radio.Group>
          </Form.Item>
        )}

        {renderEventFields()}
      </Form>
    </Modal>
  ) : (
    <></>
  );
};

export default EventForm;
