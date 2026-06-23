import axios from 'axios';

const createUserUrl = process.env.REACT_APP_LOGIN_URL;

export const createAccountFunc = async (username, password, email) => {
  if (!createUserUrl) {
    throw new Error(
      'REACT_APP_LOGIN_URL is not defined. Check your .env file and restart npm start.'
    );
  }

  try {
    const response = await axios.post(`${createUserUrl}/create-user`, {
      username,
      password,
      email
    });

    return response.data;
  } catch (error) {
    console.error('Login request failed:', error);
    throw error;
  }
};