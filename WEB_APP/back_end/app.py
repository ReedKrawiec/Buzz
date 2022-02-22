from flask import Flask, request
from flask.helpers import send_from_directory
from flask_socketio import SocketIO
import os

app = Flask(__name__,
            static_url_path='' ,
            static_folder='static')

connections = {}
# Socket.io will reject any connection that doesn't originate from one of the below domains,
# if you're self-hosting, you may have to add your domain to the list below.
socketio = SocketIO(app=app,cors_allowed_origins=["https://buzz.reed.codes","http://localhost:8000"])

if __name__ == '__main__':
    socketio.run(app)

@socketio.on('message')
def handle_message(message):
    # Register a new connection
    connections[message] = request.sid

@app.route("/")
def index():
    return app.send_static_file("index.html")

@app.route("/privacy")
def privacy():
    return app.send_static_file("privacypolicy.html")

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
