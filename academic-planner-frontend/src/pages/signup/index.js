import { Row, Col, Image, Form, Input, Button, message } from 'antd';
import { fullHeight } from '../../styles';
import Background from '../../assets/SignupBackgroundImage.jpg';
import Title from 'antd/es/typography/Title';
import { sigupnRequest } from './api';
import { useNavigate } from 'react-router-dom';
const SignupPage = () => {
  const navigate = useNavigate();

  const onFinish = (formValues) => {
    const { email, firstName, lastName, password } = formValues;
    sigupnRequest({
      email,
      firstName,
      lastName,
      passwordHash: password,
    });
    message.success('User Created Successfully');
    navigate('/login');
  };
  return (
    <Row style={{ width: '100%', height: '100vh' }}>
      <Col span={14}>
        <Image preview={false} src={Background} style={fullHeight} />
      </Col>
      <Col style={{ alignSelf: 'center' }}>
        <Title style={{ textAlign: 'center', marginBottom: '30px' }} level={2}>
          Signup
        </Title>
        <Form
          name='sign_up_form'
          layout='vertical'
          style={{ maxWidth: 600, margin: 'auto auto' }}
          onFinish={onFinish}
        >
          <Row>
            <Col span={11}>
              <Form.Item
                label='First Name'
                name='firstName'
                rules={[
                  {
                    required: true,
                    message: 'Enter your First Name!',
                  },
                ]}
              >
                <Input placeholder='Enter First Name' />
              </Form.Item>
            </Col>
            <Col span={11} offset={2}>
              <Form.Item
                label='Last Name'
                name='lastName'
                rules={[
                  {
                    required: true,
                    message: 'Enter your Last Name!',
                  },
                ]}
              >
                <Input placeholder='Enter Last Name' />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label='E-mail'
            name='email'
            rules={[
              {
                type: 'email',
                message: 'The input is not valid E-mail!',
              },
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
            name='password'
            rules={[
              { required: true, message: 'Please input your password!' },
              {
                type: 'string',
                min: 4,
                message: 'Please enter password longer than 4 character!',
                validateTrigger: 'onSubmit',
              },
            ]}
          >
            <Input.Password placeholder='Enter Password' />
          </Form.Item>
          <Form.Item
            label='Confirm Password'
            name='confirmPassword'
            dependencies={['password']}
            rules={[
              { required: true, message: 'Please confirm your password!' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(
                    new Error(
                      'The two passwords that you entered do not match!'
                    )
                  );
                },
              }),
            ]}
          >
            <Input.Password placeholder='Re-enter Password' />
          </Form.Item>
          <Row justify='space-between'>
            <Col span={13}>
              <Form.Item>
                <Button
                  type='primary'
                  htmlType='submit'
                  style={{ width: '40%' }}
                >
                  Create
                </Button>
              </Form.Item>
            </Col>
            <Col span={11}>
              <Button
                type='link'
                onClick={() => {
                  navigate('/login');
                }}
              >
                Already have an account!
              </Button>
            </Col>
          </Row>
        </Form>
      </Col>
    </Row>
  );
};

export default SignupPage;
