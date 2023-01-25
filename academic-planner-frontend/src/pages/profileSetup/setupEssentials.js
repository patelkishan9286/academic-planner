import { Button, Col, Row, Table } from 'antd';
import Title from 'antd/es/typography/Title';
import React, { useState } from 'react';
import { addEvent } from './api';
import AddScheduleForm from './components/addScheduleForm';

const SetupEssential = () => {
  const [isModalOpen, setIsModalOpen] = useState(true);
  const [dataSource, setDataSource] = useState([]);

  const handleSaveClick = (data) => {
    data = {
      ...data,
      eventCategory: 'LEISURE_TIME',
    };
    addEvent(data);
    setDataSource([...dataSource, data]);
    setIsModalOpen(false);
  };
  const columns = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Start Time',
      dataIndex: 'startTime',
      key: 'startTime',
    },
    {
      title: 'End Time',
      dataIndex: 'endTime',
      key: 'endTime',
    },
    {
      title: 'Repeat',
      dataIndex: 'weekDay',
      render: (text) => <>{text ? `Every weekDay` : 'Daily'}</>,
      key: 'weekDay',
    },
  ];
  return (
    <div>
      <Row justify='space-between'>
        <Col>
          <Title style={{ marginTop: '10px' }} level={4}>
            Essential Time Schedule
          </Title>
        </Col>
        <Col style={{ alignSelf: 'center' }}>
          <Button type='primary' onClick={() => setIsModalOpen(true)}>
            Add Schedule
          </Button>
        </Col>
      </Row>
      <Table columns={columns} dataSource={dataSource} />
      <AddScheduleForm
        isModalOpen={isModalOpen}
        handleCancel={() => setIsModalOpen(false)}
        title={'Add Essential Time Schedule'}
        handleOn={handleSaveClick}
      />
    </div>
  );
};

export default SetupEssential;
