from flask import Flask, request
from flask.helpers import send_from_directory
from flask_socketio import SocketIO
import os

app = Flask(__name__,
            static_url_path='' ,
            static_folder='static')

connections = {}

socketio = SocketIO(app=app,cors_allowed_origins=["https://dev.reed.codes","http://localhost:5000"])

if __name__ == '__main__':
    socketio.run(app)

@socketio.on('message')
def handle_message(message):
    connections[message] = request.sid
    print(connections)

@app.route("/")
def index():
    return app.send_static_file("index.html")

@app.route("/pair/<cid>")
def register_connection(cid):  
    if cid in connections:
        socketio.send("paired",to=connections[cid])
        return "1"
    return "0"
@app.route("/alert/<cid>")
def alert(cid):  
    if cid in connections:
        print("alert:" + cid)
        socketio.send("alert",to=connections[cid])
        return "1"
    return "0"