Flask backend using Docker. 

# Building
Build with
`
docker build -t <name>. 
`

# Running
`
docker run -p 5000:5000 <name>
`
The application is exposed on port 5000, and expects the front-end to be available in a folder named "static" in the back_end root.
