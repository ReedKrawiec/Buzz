import './App.css';

function generateCode(){
  let characters = ["0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"];
  return Array.from(new Array(6)).map(() => characters[Math.floor(Math.random() * characters.length)]).join("");
}

const App = () => {
  let code;
  if(localStorage.getItem("code") == null){
    code = generateCode();
    localStorage.setItem("code",code);    
  }
  else{
    code = (localStorage.getItem("code"));
  }
  console.log(code);
  fetch(`/register/${code}`);
  return (
    <div>
      Hello! Code: {code}
      <button></button>
    </div>
  )
}

export default App;
