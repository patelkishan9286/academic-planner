import React from 'react';
import { Menu, Dropdown } from 'antd';
import { EditOutlined, DeleteOutlined, CheckOutlined } from '@ant-design/icons';

const EventMenu = ({ event, onEdit, onDelete, children }) => {
  const menu = (
    <Menu>
      <Menu.Item key='edit' onClick={onEdit} icon={<EditOutlined />}>
        Edit
      </Menu.Item>
      <Menu.Item
        key='delete'
        onClick={onDelete}
        icon={
          event.eventType === 'VARIABLE' ? (
            <CheckOutlined />
          ) : (
            <DeleteOutlined />
          )
        }
      >
        {event.eventType === 'VARIABLE' ? 'Done' : 'Delete'}
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={menu} trigger={['click']}>
      <div style={{ cursor: 'pointer' }}>{children}</div>
    </Dropdown>
  );
};

export default EventMenu;
