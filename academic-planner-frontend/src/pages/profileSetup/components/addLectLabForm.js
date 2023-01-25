import React from 'react';
import { TimePicker, Modal, Form, Input, Radio, Row, Col, Select } from 'antd';
import { ArrayOfWeekDays, WeekDays } from '../contsants';

const AddLectLabForm = (props) => {
  const [form] = Form.useForm();

  const onSubmit = () => {
    let weeklyRepeatDays = [false, false, false, false, false, false, false];
    form
      .validateFields()
      .then((values) => {
        if (values.weekDays)
          values.weekDays.map((weekDay) => (weeklyRepeatDays[weekDay] = true));
        values = {
          ...values,
          startDate: new Date().toISOString().slice(0, 10),
          endDate: '2023-12-31',
          startTime: `${values.startTime.format('HH:mm')}`,
          endTime: `${values.endTime.format('HH:mm')}`,
          isReschedulable: false,
          isRepeat: true,
          eventPriority: 'HIGH',
          repeatEvent: {
            repititionType: 'WEEKLY',
            weeklyRepeatDays,
            endDate: '2023-12-31',
          },
          weekDay: ArrayOfWeekDays[values.weekDay],
        };
        form.resetFields();
        props.handleOk(values);
      })
      .catch((err) => {});
  };
  // WeekDays;
  return (
    <Modal
      title={props.title}
      open={props.isModalOpen}
      onOk={onSubmit}
      okText={'Save'}
      onCancel={props.handleCancel}
    >
      <Form form={form}>
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
        {props.isLectLab && (
          <Form.Item
            label='Class Type'
            name='eventCategory'
            rules={[
              {
                required: 'true',
                message: 'Select Class Type!',
              },
            ]}
          >
            <Radio.Group>
              <Radio.Button value='CLASS'>Lecture</Radio.Button>
              <Radio.Button value='LAB'>Laboratory</Radio.Button>
            </Radio.Group>
          </Form.Item>
        )}
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
          name='weekDays'
          label='Day of Week'
          hasFeedback
          rules={[{ required: true, message: 'Please select week day(s)!' }]}
        >
          <Select
            mode='multiple'
            allowClear
            placeholder='Select week day(s)'
            options={WeekDays}
          ></Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddLectLabForm;
