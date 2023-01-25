# Academic Planner

Academic Planner is a tailored calendar application designed specifically for university students. It intelligently schedules and organizes your academic and personal events, ensuring you stay on top of your busy life.

## Features

1. **User Authentication**: Secure registration and login with email verification.
2. **Profile Setup**: Customize your schedule by adding class timings, part-time jobs, and leisure activities.
3. **Event Management**: Easily add, update, or delete events. Schedule events with deadlines and allocate time for each.
4. **Recurring Events**: Set events to repeat daily or weekly.
5. **Task Completion**: Mark events as done and declutter your calendar view.
6. **Smart Rescheduling**: Reschedule events with a single click. Get notified if any event can't be scheduled.

## Tech Stack

### Backend:
- [Java](https://www.java.com/en/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Java JWT](https://mvnrepository.com/artifact/com.auth0/java-jwt/0.11.2)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL](https://www.postgresql.org/)

### Frontend:

- [React](https://reactjs.org/)
- [antd](https://ant.design/)
- [axios](https://axios-http.com/)
- [react-big-calendar](https://github.com/jquense/react-big-calendar)
- [redux](https://redux.js.org/)
- [react-redux](https://react-redux.js.org/)

## Getting Started

1. **Clone the repository**:
   ```bash
   git clone [current_repo]
    ```
2. **Backend Setup**:
    - Navigate to the backend directory.
    - Install the required dependencies: `mvn install`
    - Update the application.properties file with your database credentials.
    - Run the application.

3. **Frontend Setup**:
    - Navigate to the frontend directory from the root: cd frontend
    - Install the required dependencies: npm install
    - Start the frontend server: npm start
    - The application should now be running on http://localhost:3000/