import { Button } from 'antd';
import Title from 'antd/es/typography/Title';
import React, { useState } from 'react';
import SetupEssential from './setupEssentials';
import SetupJob from './setupJob';
import SetupLectLab from './setupLectLab';
import SetupPersonalActivity from './setupPersonalActivity';
import { useNavigate } from 'react-router-dom';
import { setUserProfileComplete } from './api';

const ProfieSetup = () => {
  const [currentStep, setCurrentStep] = useState(1);
  const navigate = useNavigate();

  const handleNext = () => {
    if (currentStep > 3) {
      navigate('/home');
      setUserProfileComplete();
    }
    setCurrentStep(currentStep + 1);
  };

  const renderContent = () => {
    switch (currentStep) {
      case 1:
        return <SetupLectLab />;
      case 2:
        return <SetupJob />;
      case 3:
        return <SetupEssential />;
      case 4:
        return <SetupPersonalActivity />;
      default:
        return <></>;
    }
  };

  return (
    <div>
      <Title level={2}>Profile Setup</Title>
      <div
        style={{
          background: '#D7D7D7',
          borderRadius: '5px',
          padding: '0 10px 0',
          margin: '20px',
          paddingBottom: '10px',
        }}
      >
        {renderContent()}
      </div>
      <Button
        style={{ marginLeft: '20px' }}
        type='primary'
        onClick={handleNext}
      >
        {currentStep > 3 ? 'Finish' : 'Next'}
      </Button>
    </div>
  );
};

export default ProfieSetup;
