import { Button, Col, Row, Table } from 'antd';
import Title from 'antd/es/typography/Title';
import React, { useState } from 'react';
import { addEvent } from './api';
import AddLectLabForm from './components/addLectLabForm';

const SetupLectLab = () => {
  const [isModalOpen, setIsModalOpen] = useState(true);
  const [dataSource, setDataSource] = useState([]);

  const handleSaveClick = (data) => {
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
      title: 'Type',
      dataIndex: 'eventCategory',
      key: 'type',
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
      title: 'Week Day',
      dataIndex: 'weekDay',
      key: 'weekDay',
    },
  ];

  return (
    <div>
      <Row justify='space-between'>
        <Col>
          <Title style={{ marginTop: '10px' }} level={4}>
            Lecture & Lab Schedule
          </Title>
        </Col>
        <Col style={{ alignSelf: 'center' }}>
          <Button type='primary' onClick={() => setIsModalOpen(true)}>
            Add Schedule
          </Button>
        </Col>
      </Row>
      <Table columns={columns} dataSource={dataSource} />
      <AddLectLabForm
        isModalOpen={isModalOpen}
        handleCancel={() => setIsModalOpen(false)}
        title={'Add Lecture and Lab Schedule'}
        isLectLab={true}
        handleOk={handleSaveClick}
      />
    </div>
  );
};

export default SetupLectLab;
