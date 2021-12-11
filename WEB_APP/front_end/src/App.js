import './App.css';
import { io } from "socket.io-client";
import { ReactComponent as Clock } from "./Clock.svg"
import React, { useState, useEffect, useReducer, useRef } from 'react';

function generateCode(){
  let characters = ["0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"];
  //This generates a random 6 length string made with characters from the characters array
  return Array.from(new Array(6)).map(() => characters[Math.floor(Math.random() * characters.length)]).join("");
}

const socket = io({
  path: "/socket.io"
});

// Component for displaying an alert
const Alert = (props) => {
  return (<div className="alert">
      <div className="alertLeft">
        <Clock />
        <span className="alertTime">{props.time}</span>
      </div>
      <div className="alertRight">
        <p className="alertLargeText">Phone alert was triggered!</p>
      </div>
  </div>);
}

const PairNotice = (props) => {
  if(props.paired){
    return <p className="contentLines">Phone has paired!</p>
  }
  return <p className="contentLines">Waiting for a phone to pair ...</p>;
}

const App = () => {
  let code;
  // First, check whether the user has a code stored in local storage
  if(localStorage.getItem("code") == null){
    //If not, generate a new code and store it in local storage
    code = generateCode();
    localStorage.setItem("code",code);    
  }
  else{
    code = (localStorage.getItem("code"));
  }
  // Use a reference, so that we can access the alerts array in the socker message event handler
  const alerts = useRef([]);
  const [useAlerts,_setAlerts] = useState([]);
  const [hasPaired,setPaired] = useState(false)
  useEffect(()=>{
    socket.on('message', function (message) {
      console.log(message);
      if(message == "alert"){
        Notification.requestPermission(function(status) {
          if(Notification.permission == 'granted'){
            var notification = new Notification("Alert!",{
              icon:"./logo192.png"
            });
            let now = new Date();
            alerts.current = [now.toLocaleTimeString(),...alerts.current];
            _setAlerts(alerts.current);
          }
        });
      } else if(message == "paired"){
        setPaired(true);
      }
    });
  },[]);
  
  // Sends the current code to the server
  useEffect(()=>{
    socket.send(code);
  },[code]);

  return (
    <div>
      <div className="contentContainer">
        <p className="contentLines">Your code is <span className="code">{code}</span></p>
        <PairNotice paired={hasPaired} />
        {useAlerts.current}        
        <div className="alertsContainer">
          {useAlerts.map((time)=><Alert time={time}/>)}
        </div>
      </div>
    </div>
  )
}

export default App;
