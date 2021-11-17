from flask import Flask, request
from flask.helpers import send_from_directory
from flask_socketio import SocketIO
import os

app = Flask(__name__,
            static_url_path='' ,
            static_folder='static')

connections = {}

socketio = SocketIO(app)

if __name__ == '__main__':
    socketio.run(app)

@socketio.on('message')
def handle_message(message):
    connections[message] = request.sid
    print(connections)
#socketio.send("alert",to=request.sid)

@app.route("/")
def index():
    return app.send_static_file("index.html")

@app.route("/register/<cid>")
def register_connection(cid):  
    if not cid in connections:
        connections[cid] = "test"
    print(connections) 
    return "<p>Hello," + connections[cid] + "World!</p>"

@app.route("/alert/<cid>")
def alert(cid):  
    if cid in connections:
        print("alert:" + cid)
        socketio.send("alert",to=connections[cid])
        return "1"
    return "0"