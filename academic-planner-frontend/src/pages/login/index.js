import { Row, Col, Image, Form, Input, Button, message } from 'antd';
import { fullHeight } from '../../styles';
import Background from '../../assets/SignupBackgroundImage.jpg';
import { useNavigate } from 'react-router-dom';
import Title from 'antd/es/typography/Title';
import { loginRequest } from './api';
import { useEffect } from 'react';

const LoginPage = () => {
  const navigate = useNavigate();

  useEffect(() => {
    if (localStorage.getItem('jwtToken')) {
      navigate('/home');
    }
    // eslint-disable-next-line
  }, []);

  const onFinish = async (formValues) => {
    try {
      const response = await loginRequest(formValues);
      const token = response.data.token;
      localStorage.setItem('jwtToken', token);

      if (response.data.profileStatus === 'SET') {
        navigate('/home');
      } else {
        navigate('/profile-setup');
      }
    } catch (error) {
      message.error(error.response.data);
    }
  };
  return (
    <Row style={{ width: '100%', height: '100vh' }}>
      <Col span={14}>
        <Image preview={false} src={Background} style={fullHeight} />
      </Col>
      <Col span={6} style={{ alignSelf: 'center' }}>
        <Title style={{ textAlign: 'center', marginBottom: '30px' }} level={2}>
          Login
        </Title>
        <Form
          name='login_form'
          layout='vertical'
          style={{ maxWidth: 600, margin: 'auto auto' }}
          onFinish={onFinish}
        >
          <Form.Item
            label='E-mail'
            name='email'
            rules={[
              {
                required: true,
                message: 'Please input your E-mail!',
              },
            ]}
          >
            <Input placeholder='Enter E-mail' />
          </Form.Item>

          <Form.Item
            label='Password'
            name='passwordHash'
            rules={[{ required: true, message: 'Please input your password!' }]}
          >
            <Input.Password placeholder='Enter Password' />
          </Form.Item>
          <Row justify='space-between'>
            <Col span={9}>
              <Form.Item>
                <Button type='primary' htmlType='submit'>
                  Login
                </Button>
              </Form.Item>
            </Col>
            <Col>
              <Button
                type='link'
                onClick={() => {
                  navigate('/sign-up');
                }}
              >
                New User! Click here
              </Button>
            </Col>
          </Row>
        </Form>
      </Col>
    </Row>
  );
};

export default LoginPage;
