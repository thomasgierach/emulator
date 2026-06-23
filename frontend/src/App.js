import { Routes, Route, Link } from "react-router-dom";
import Login from "./Login";
import CreateAccount from "./CreateAccount";

function App() {
  return (
    <div>
      <nav>
        <Link to="/">Login</Link>
        {" | "}
        <Link to="/create-account">Create Account</Link>
      </nav>

      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/create-account" element={<CreateAccount />} />
      </Routes>
    </div>
  );
}

export default App;
