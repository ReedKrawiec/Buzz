import './App.css';
import { io } from "socket.io-client";
import React, { useState, useEffect } from 'react';
function generateCode(){
  let characters = ["0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"];
  return Array.from(new Array(6)).map(() => characters[Math.floor(Math.random() * characters.length)]).join("");
}

const socket = io();

socket.on('message', function (message) {
  if(message == "alert"){
    Notification.requestPermission(function(status) {
      if(Notification.permission == 'granted'){
        var notification = new Notification("Alert!",{
          icon:"./logo192.png"
        });
      }
    });
  }
});

const App = () => {
  let code;
  if(localStorage.getItem("code") == null){
    code = generateCode();
    localStorage.setItem("code",code);    
  }
  else{
    code = (localStorage.getItem("code"));
  }
  
  useEffect(()=>{
    socket.send(code);
  },[code])

  return (
    <div>
      Hello! Code: {code}
      <button></button>
    </div>
  )
}

export default App;
