import './App.css';
import { io } from "socket.io-client";
import React, { useState, useEffect, useReducer } from 'react';
function generateCode(){
  let characters = ["0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"];
  return Array.from(new Array(6)).map(() => characters[Math.floor(Math.random() * characters.length)]).join("");
}

const socket = io();




const Alert = (props) => {
  return (<div className="alert">
      <div className="alertLeft">
        {props.time}
      </div>
      <div className="alertRight">
        <p className="alertLargeText">Phone alert was triggered!</p>
      </div>
  </div>);
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
  
  const [useAlerts,setAlerts] = useState([]);
  
  useEffect(()=>{
    socket.on('message', function (message) {
      if(message == "alert"){
        Notification.requestPermission(function(status) {
          if(Notification.permission == 'granted'){
            var notification = new Notification("Alert!",{
              icon:"./logo192.png"
            });
            let now = new Date();
            setAlerts([now.toLocaleTimeString(),...useAlerts]);
          }
        });
      }
    });
  })

  useEffect(()=>{
    socket.send(code);
  },[code])

  return (
    <div>
      <div className="contentContainer">
        <p className="contentLines">Your code is <span className="code">{code}</span></p>
        <p className="contentLines">Waiting for a phone to pair ...</p>
        <div className="alertsContainer">
          {useAlerts.map((time)=><Alert time={time}/>)}
        </div>
      </div>
    </div>
  )
}

export default App;
