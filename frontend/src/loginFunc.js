import axios from 'axios';

const loginUrl = process.env.REACT_APP_LOGIN_URL;

export const loginFunc = async (username, password) => {
  if (!loginUrl) {
    throw new Error(
      'REACT_APP_LOGIN_URL is not defined. Check your .env file and restart npm start.'
    );
  }

  try {
    const response = await axios.post(`${loginUrl}/login`, {
      username,
      password,
    });

    return response.data;
  } catch (error) {
    console.error('Login request failed:', error);
    throw error;
  }
};