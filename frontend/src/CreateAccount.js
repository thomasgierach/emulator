// CreateAccount.js
import React, { useState } from 'react';
import './CreateAccount.css';
import { createAccountFunc } from './createAccountFunc';

function CreateAccount() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();

    setMessage('');
    setError('');

    try {
      await createAccountFunc({
        username,
        email,
        password
      });

      setMessage('User created successfully.');
      setUsername('');
      setEmail('');
      setPassword('');
    } catch (err) {
      console.error(err);
      setError('Unable to create user.');
    }
  }

  return (
    <div className="container mt-4">
      <h1>Create User</h1>

      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Username</label>
          <input
            className="form-control"
            type="text"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            className="form-control"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Password</label>
          <input
            className="form-control"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />
        </div>

        <button className="btn btn-primary" type="submit">
          Create Account
        </button>
      </form>
    </div>
  );
}

export default CreateAccount;